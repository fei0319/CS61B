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
     * If the branch does not exist, return null.
     *
     * @param branchName name of the branch
     * @return SHA-1 value of corresponding commit
     * or null if the branch does not exist;
     */
    public static String getBranch(String branchName) {
        File f = Utils.join(GITLET_REF_DIR, branchName);
        if (!f.exists())
            return null;
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
     * Remove the specified branch. No specification for
     * non-existent branch.
     *
     * @param branchName branch to delete
     */
    public static void removeBranch(String branchName) {
        File f = Utils.join(GITLET_REF_DIR, branchName);
        f.delete();
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
        commit(message, null);
    }

    private static void commit(String message, String parent) {
        Staged stagingArea = (Staged) GitletObject.readAndDeleteUnused(getRef("STAGED"));
        Commit current = (Commit) GitletObject.read(getBranch(getRef("HEAD")));

        if (stagingArea.isEmpty()) {
            Utils.exit("No changes added to the commit.");
        }
        if (message.isEmpty()) {
            Utils.exit("Please enter a commit message.");
        }

        Commit commit = current.nextCommit(message, stagingArea);
        if (parent != null)
            commit.addParent(parent);
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
        boolean nothingFound = true;
        for (GitletObject object : GitletObject.listObjects()) {
            if (object.getClass().equals(Commit.class)) {
                Commit commit = (Commit) object;
                if (commit.getMessage().equals(message)) {
                    Utils.message(commit.sha1());
                    nothingFound = false;
                }
            }
        }
        if (nothingFound)
            Utils.exit("Found no commit with that message.");
    }

    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     */
    public static void status() {
        Staged stagingArea = (Staged) GitletObject.readAndDeleteUnused(getRef("STAGED"));
        Commit current = (Commit) GitletObject.read(getBranch(getRef("HEAD")));

        List<String> branches = getBranches();
        ArrayList<File> staged = new ArrayList<>(), removed = new ArrayList<>();
        for (Map.Entry<File, String> change : stagingArea.getChanges().entrySet()) {
            if (change.getValue() == null) {
                removed.add(change.getKey());
            } else {
                staged.add(change.getKey());
            }
        }
        branches.sort(null);
        staged.sort(null);
        removed.sort(null);

        Utils.message("=== Branches ===");
        for (String branch : branches)
            Utils.message((branch.equals(getRef("HEAD")) ? "*" : "") + branch);
        Utils.message("");

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

    /**
     * CLEAR ALL PLAIN FILES IN CWD
     */
    private static void clearCWD() {
        List<String> fileNames = Utils.plainFilenamesIn(CWD);
        if (fileNames == null)
            return;
        for (String fileName : fileNames)
            new File(fileName).delete();
    }

    /**
     * WILL CLEAR CWD IN ADVANCE
     * Takes all files in the specified commit and puts them in CWD.
     * The staging area will also be cleared.
     * No specification on non-existent commit.
     *
     * @param commitName commit to check out
     */
    private static void checkoutCommit(String commitName) {
        Commit current = (Commit) GitletObject.read(getBranch(getRef("HEAD")));
        if (Utils.plainFilenamesIn(CWD) != null) {
            for (String s : Utils.plainFilenamesIn(CWD)) {
                File f = new File(s);
                if (!new Blob(f).sha1().equals(current.getFile(f)))
                    Utils.exit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        
        Commit commit = (Commit) GitletObject.read(commitName);
        clearCWD();

        assert commit != null;
        for (Map.Entry<File, String> track : commit.getTracked().entrySet())
            ((Blob) GitletObject.read(track.getValue())).saveAs(track.getKey());
        Staged stagingArea = (Staged) GitletObject.readAndDeleteUnused(getRef("STAGED"));
        stagingArea.clear();
        stagingArea.store();
    }

    /**
     * WILL CLEAR CWD IN ADVANCE
     * Takes all files in the commit at the head of the given branch, and puts
     * them in the working directory, overwriting the versions of the files that
     * are already there if they exist.
     *
     * @param branchName branch to check out
     */
    public static void checkoutBranch(String branchName) {
        if (getBranch(branchName) == null)
            Utils.exit("No such branch exists.");
        if (branchName.equals(getRef("HEAD")))
            Utils.exit("No need to checkout the current branch.");

        checkoutCommit(getBranch(branchName));
        setRef("HEAD", branchName);
    }

    /**
     * Takes the version of the file as it exists in the commit with
     * the given id, and puts it in the working directory, overwriting
     * the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     *
     * @param commitName version of the checked out file
     * @param fileName   file to check out
     */
    public static void checkoutFile(String commitName, String fileName) {
        commitName = GitletObject.autocomplete(commitName);
        GitletObject object = GitletObject.read(commitName);
        if (object == null)
            Utils.exit("No commit with that id exists.");

        Commit commit = (Commit) object;
        File f = new File(fileName);
        if (!commit.hasFile(f))
            Utils.exit("File does not exist in that commit.");
        ((Blob) GitletObject.read(commit.getFile(f))).saveAs(f);
    }

    /**
     * Creates a new branch with the given name, and points it at
     * the current head commit. A branch is nothing more than a name
     * for a reference (a SHA-1 identifier) to a commit node.
     *
     * @param branchName branch to create
     */
    public static void branch(String branchName) {
        if (getBranch(branchName) != null)
            Utils.exit("A branch with that name already exists.");
        setBranch(branchName, getBranch(getRef("HEAD")));
    }

    /**
     * Deletes the branch with the given name. This only means to delete
     * the pointer associated with the branch; it does not mean to delete
     * all commits that were created under the branch, or anything like
     * that.
     *
     * @param branchName branch to delete
     */
    public static void rmBranch(String branchName) {
        if (getBranch(branchName) == null)
            Utils.exit("A branch with that name does not exist.");
        if (getRef("HEAD").equals(branchName))
            Utils.exit("Cannot remove the current branch.");
        removeBranch(branchName);
    }

    /**
     * The command is essentially checkout of an arbitrary commit that
     * also changes the current branch head.
     *
     * @param commitName commit to reset to
     */
    public static void reset(String commitName) {
        commitName = GitletObject.autocomplete(commitName);
        if (commitName == null)
            Utils.exit("No commit with that id exists.");
        checkoutCommit(commitName);
        setBranch(getRef("HEAD"), commitName);
    }

    /**
     * Merges the specified branch to current branch.
     * For each file, if it is changed in exactly one branch
     * since the diverged point, checks it out and stages it
     * for commit; if it is changed in both branches but in
     * the same way, leaves it as it is; otherwise, it is
     * considered to be a conflict.
     *
     * @param branchName branch to merge with
     */
    public static void merge(String branchName) {
        Staged staged = (Staged) GitletObject.read(getRef("STAGED"));
        if (!staged.isEmpty())
            Utils.exit("You have uncommitted changes.");

        if (GitletObject.read(getBranch(branchName)) == null)
            Utils.exit("A branch with that name does not exist.");

        if (branchName.equals(getRef("HEAD")))
            Utils.exit("Cannot merge a branch with itself.");

        Commit current = (Commit) GitletObject.read(getBranch(getRef("HEAD"))),
                branch = (Commit) GitletObject.read(getBranch(branchName));
        assert current != null;

        Commit div = Commit.lowestCommonAncestor(current, branch);

        if (div.sha1().equals(branch.sha1()))
            Utils.exit("Given branch is an ancestor of the current branch.");
        if (div.sha1().equals(current.sha1())) {
            checkoutBranch(branchName);
            Utils.exit("Current branch fast-forwarded.");
        }

        Staged deltaThis = Staged.delta(div, current),
                deltaThat = Staged.delta(div, branch);

        Set<File> changedFiles = new HashSet<>();
        changedFiles.addAll(List.of(deltaThis.stagedFiles()));
        changedFiles.addAll(List.of(deltaThat.stagedFiles()));

        boolean encounteredConflict = false;
        for (File f : changedFiles) {
            if (deltaThis.hasFile(f) && deltaThat.hasFile(f)) {
                String inThis = deltaThis.getFile(f),
                        inThat = deltaThat.getFile(f);

                if ((inThis == null && inThat != null) ||
                        (inThis != null && !inThis.equals(inThat))) {
                    Blob.conflict(current.getFile(f), branch.getFile(f)).saveAs(f);
                    encounteredConflict = true;
                    staged.add(current, f);
                }
            } else if (deltaThat.hasFile(f)) {
                String s = branch.getFile(f);
                if (s == null) {
                    if (f.exists())
                        f.delete();
                    staged.stageForRemoval(f);
                } else {
                    ((Blob) GitletObject.read(s)).saveAs(f);
                    staged.add(current, f);
                }
            }
        }

        staged.store();
        commit(String.format("Merged %s into %s.", branchName, getRef("HEAD")),
                branch.sha1());
        if (encounteredConflict)
            Utils.exit("Encountered a merge conflict.");
    }
}
