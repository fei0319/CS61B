package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
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
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */

    /**
     * Get the SHA-1 value of the specified ref.
     *
     * @return SHA-1 value of the specified ref
     */
    public static String getRef(String ref) {
        File f = Utils.join(GITLET_DIR, ref);
        return Utils.readContentsAsString(f);
    }

    /**
     * Set the SHA1 value of a specified ref.
     *
     * @param ref the ref to be set
     * @param val the SHA-1 value to be applied
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
            Commit initialCommit = new Commit();
            Staged stagingArea = new Staged();
            initialCommit.store();
            stagingArea.store();

            setRef("HEAD", initialCommit.sha1());
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
        Staged stagingArea = (Staged) GitletObject.read(getRef("STAGED"));
        Commit current = (Commit) GitletObject.read(getRef("HEAD"));
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
        Staged stagingArea = (Staged) GitletObject.read(getRef("STAGED"));
        Commit current = (Commit) GitletObject.read(getRef("HEAD"));

        if (stagingArea.isEmpty()) {
            Utils.exit("No changes added to the commit.");
        }
        if (message.isEmpty()) {
            Utils.exit("Please enter a commit message.");
        }

        Commit commit = current.nextCommit(message, stagingArea);
        commit.store();
        setRef("HEAD", commit.sha1());
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
        Staged stagingArea = (Staged) GitletObject.read(getRef("STAGED"));
        if (stagingArea.rm(new File(fileName)))
            return;
        // TODO: Rest part of rm
    }

    /**
     * Starting at the current head commit, display information about
     * each commit backwards along the commit tree until the initial commit,
     * following the first parent commit links, ignoring any second parents found in merge commits.
     * In regular Git, this is what you get with git log --first-parent.
     */
    public static void log() {
        String currentSHA1 = getRef("HEAD");
        while (true) {
            Commit commit = (Commit) GitletObject.read(currentSHA1);
            commit.show();
            if (commit.isInitial())
                break;
            currentSHA1 = commit.getParents()[0];
        }
    }
}
