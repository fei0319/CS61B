package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */

    /**
     * Get the SHA1 value of HEAD.
     * @return SHA1 value of HEAD
     */
    public static String getHead() {
        File f = Utils.join(GITLET_DIR, "HEAD");
        return Utils.readContentsAsString(f);
    }

    /**
     * Set the SHA1 value of HEAD.
     * @param head SHA1 value of HEAD
     */
    public static void setHead(String head) {
        File f = Utils.join(GITLET_DIR, "HEAD");
        Utils.writeContents(f, head);
    }

    /**
     * Initialize a gitlet repository.It is invoked by the following command:
     * <pre>{@code gitlet init}</pre>
     */
    public static void init() {
        if (!GITLET_DIR.exists()) {
            Commit initialCommit = new Commit();
            initialCommit.store(GITLET_DIR);
            setHead(initialCommit.sha1());
        }
        else
            Utils.exit("A Gitlet version-control system already exists in the current directory.");
    }

    public static void log() {
        String currentSHA1 = getHead();
        while (true) {
            Commit commit = (Commit) GitletObject.read(GITLET_DIR, currentSHA1);
            commit.show();
            if (commit.isInitial())
                break;
            currentSHA1 = commit.getParents()[0];
        }
    }
}
