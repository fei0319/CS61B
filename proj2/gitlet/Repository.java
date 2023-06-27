package gitlet;

import java.io.File;
import java.util.*;

/**
 * Represents a gitlet repository.
 * <p>
 * In gitlet, there are three kinds of {@link GitletObject}, respectively
 * {@link Commit}, {@link Blob} and {@link Staged}. Commit is used to store
 * commits, Blob is used to store files and Staged represents staging area.
 * All these objects are stored in {@link Repository#GITLET_DIR}/objects,
 * with their SHA-1 value being their names.
 * <p>
 * Two single file {@code HEAD} and {@code STAGED} are stored in
 * {@link Repository#GITLET_DIR}. {@code HEAD} stores name of the current
 * branch and {@code STAGED} stores SHA-1 value of the staging area object.
 * <p>
 * Branches are stored in {@link Repository#GITLET_REF_DIR}, one each file.
 * Each file contains SHA-1 value of the topmost commit of the branch and
 * was named by the branch.
 *
 * @author Fei Pan
 */
public class Repository {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /**
     * The branches directory.
     */
    public static final File GITLET_REF_DIR = Utils.join(GITLET_DIR, "refs");

    /* TODO: fill in the rest of this class. */

    /**
     * Get a list of branch names.
     *
     * @return a list of branch names
     */
    public static List<String> getBranches() {
        return Utils.plainFilenamesIn(GITLET_REF_DIR);
    }

    /**
     * Get SHA-1 value of the commit the specified branch points to.
     *
     * @param branchName name of the branch
     * @return SHA-1 value of corresponding commit
     */
    public static String getBranch(String branchName) {
        File f = Utils.join(GITLET_REF_DIR, branchName);
        return Utils.readContentsAsString(f);
    }

    /**
     * Make the specified branch point to the specified commit.
     * If such branch does not exist, create it.
     *
     * @param branchName branch to set
     * @param commitName commit to point to
     */
    public static void setBranch(String branchName, String commitName) {
        File f = Utils.join(GITLET_REF_DIR, branchName);
        Utils.writeContents(f, commitName);
    }

    /**
     * Get the SHA-1 value of the specified ref.
     * If the specified ref is HEAD, will return
     * the branch relating to it instead.
     *
     * @param ref reference to get
     * @return SHA-1 value of the specified ref,
     * or current branch for HEAD ref
     */
    public static String getRef(String ref) {
        File f = Utils.join(GITLET_DIR, ref);
        return Utils.readContentsAsString(f);
    }

    /**
     * Set the SHA1 value of a specified ref.
     * If the specified ref is HEAD, will set
     * the branch it points to rather than SHA-1
     * value.
     *
     * @param ref the ref to be set
     * @param val the SHA-1 value to be applied,
     *            or the branch to be applied for
     *            HEAD
     */
    public static void setRef(String ref, String val) {
        File f = Utils.join(GITLET_DIR, ref);
        Utils.writeContents(f, val);
    }

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit:
     * a commit that contains no files and has the commit message "initial commit"
     * and timestamp of Unix epoch.
     * It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch.
     */
    public static void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_REF_DIR.mkdirs();

            Commit initialCommit = new Commit();
            Staged stagingArea = new Staged();
            initialCommit.store();
            stagingArea.store();

            setBranch("master", initialCommit.sha1());
            setRef("HEAD", "master");
            setRef("STAGED", stagingArea.sha1());
        } else
            Utils.exit("A Gitlet version-control system already exists in the current directory.");
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area.
     * Staging an already-staged file overwrites the previous entry in the staging area.
     * If the current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there.
     *
     * @param fileName file to add
     */
    public static void add(String fileName) {
        Staged stagingArea = (Staged) GitletObject.readAndDeleteUnused(getRef("STAGED"));
        Commit current = (Commit) GitletObject.read(getBranch(getRef("HEAD")));
        stagingArea.add(current, new File(fileName));
        stagingArea.store();
    }

    /**
     * Saves a snapshot of tracked files in the current commit and staging area.
     * A commit will only update the contents of files it is tracking that
     * have been staged for addition at the time of commit.
     * Files tracked in the current commit may be untracked in the new commit as
     * a result being staged for removal by the rm command.
     *
     * @param message message for the commit
     */
    public static void commit(String message) {
        // TODO: Add branch feature
        Staged stagingArea = (Staged) GitletObject.readAndDeleteUnused(getRef("STAGED"));
        Commit current = (Commit) GitletObject.read(getBranch(getRef("HEAD")));

        if (stagingArea.isEmpty()) {
            Utils.exit("No changes added to the commit.");
        }
        if (message.isEmpty()) {
            Utils.exit("Please enter a commit message.");
        }

        Commit commit = current.nextCommit(message, stagingArea);
        commit.store();
        setBranch(getRef("HEAD"), commit.sha1());
        stagingArea.store();
        // No need to reset STAGED as Stage.store has done so
    }

    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it for removal and
     * remove the file from the working directory if the user has not already done so.
     * Will not remove it unless it is tracked in the current commit.
     *
     * @param fileName file to remove
     */
    public static void rm(String fileName) {
        Staged stagingArea = (Staged) GitletObject.readAndDeleteUnused(getRef("STAGED"));
        Commit current = (Commit) GitletObject.read(getBranch(getRef("HEAD")));
        File file = new File(fileName);
        if (stagingArea.rm(file)) {
            stagingArea.store();
            return;
        }
        if (current.hasFile(file)) {
            stagingArea.stageForRemoval(file);
            Utils.restrictedDelete(file);
            stagingArea.store();
            return;
        }
        Utils.exit("No reason to remove the file.");
    }

    /**
     * Starting at the current head commit, display information about
     * each commit backwards along the commit tree until the initial commit,
     * following the first parent commit links, ignoring any second parents found in merge commits.
     * In regular Git, this is what you get with git log --first-parent.
     */
    public static void log() {
        String currentSHA1 = getBranch(getRef("HEAD"));
        while (true) {
            Commit commit = (Commit) GitletObject.read(currentSHA1);
            commit.show();
            if (commit.isInitial())
                break;
            currentSHA1 = commit.getParents()[0];
        }
    }

    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits is not defined.
     */
    public static void globalLog() {
        for (GitletObject object : GitletObject.listObjects()) {
            if (object.getClass().equals(Commit.class))
                ((Commit) object).show();
        }
    }

    /**
     * Prints out the ids of all commits that have the given commit message, one per line.
     * If there are multiple such commits, it prints the ids out on separate lines.
     *
     * @param message message
     */
    public static void find(String message) {
        for (GitletObject object : GitletObject.listObjects()) {
            if (object.getClass().equals(Commit.class)) {
                Commit commit = (Commit) object;
                if (commit.getMessage().equals(message))
                    Utils.message(commit.sha1());
            }
        }
    }

    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     * <p>
     * TODO: Add branch feature
     */
    public static void status() {
        Staged stagingArea = (Staged) GitletObject.readAndDeleteUnused(getRef("STAGED"));
        Commit current = (Commit) GitletObject.read(getBranch(getRef("HEAD")));

        ArrayList<File> staged = new ArrayList<>(), removed = new ArrayList<>();
        for (Map.Entry<File, String> change : stagingArea.getChanges().entrySet()) {
            if (change.getValue() == null) {
                removed.add(change.getKey());
            } else {
                staged.add(change.getKey());
            }
        }
        staged.sort(null);
        removed.sort(null);

        Utils.message("=== Staged Files ===");
        for (File f : staged)
            Utils.message(f.getName());
        Utils.message("");

        Utils.message("=== Removed Files ===");
        for (File f : removed)
            Utils.message(f.getName());
        Utils.message("");

        ArrayList<File> unstaged = new ArrayList<>(), untracked = new ArrayList<>();
        for (String fileName : Utils.plainFilenamesIn(CWD)) {
            File file = new File(fileName);
            String currentVersion = current.getFile(file);
            if (stagingArea.hasFile(file)) {
                currentVersion = stagingArea.getChanges().get(file);
            }
            if (currentVersion == null && !stagingArea.containsRemoval(file))
                untracked.add(file);
            else if (!new Blob(file).sha1().equals(currentVersion))
                unstaged.add(file);
        }

        Set<File> allTrackedFiles = new HashSet<>(current.getTracked().keySet());
        allTrackedFiles.addAll(stagingArea.getChanges().keySet());
        for (File file : allTrackedFiles) {
            if (!file.exists() && !stagingArea.containsRemoval(file))
                unstaged.add(file);
        }
        unstaged.sort(null);
        untracked.sort(null);

        Utils.message("=== Modifications Not Staged For Commit ===");
        for (File f : unstaged)
            Utils.message(f.getName() + (f.exists() ? " (modified)" : " (deleted)"));
        Utils.message("");

        Utils.message("=== Untracked Files ===");
        for (File f : untracked)
            Utils.message(f.getName());
        Utils.message("");
    }
}
