package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * TODO: It's a good idea to give a description here of what else this Class
 * does at a high level.
 *
 * @author TODO
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
     * Initialize a gitlet repository. It is invoked by the following command:
     * <pre>{@code gitlet init}</pre>
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
     * Add a file to the staging area. It is invoked by the following command:
     * <pre>{@code gitlet init}</pre>
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
     * Commit. It is invoked by the following command:
     * <pre>{@code gitlet commit}</pre>
     *
     * @param message message for the commit
     */
    public static void commit(String message) {
        Staged stagingArea = (Staged) GitletObject.read(getRef("STAGED"));
        Commit current = (Commit) GitletObject.read(getRef("HEAD"));

        Commit commit = current.nextCommit(message, stagingArea);
        commit.store();
        setRef("HEAD", commit.sha1());
        stagingArea.store();
        // Not need to reset STAGED as Stage.store has done so
    }

    /**
     * Show logs. It is invoked by the following command:
     * <pre>{@code gitlet log}</pre>
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
