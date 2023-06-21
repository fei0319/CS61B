package gitlet;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

/**
 * Represents the staging area and can be converted to a commit.<br>
 * The purpose of implementing GitletObject is to make sure a Stage object can be stored like a GitletObject.
 * @author Fei Pan
 */
public class Stage implements GitletObject {
    private HashMap<File, String> changes;
    public String sha1() {
        return Utils.sha1((Object) Utils.serialize(changes));
    }
    public void add(File file) {}
    public void rm(File file) {}

    /**
     * Clear the staging area.
     */
    public void clear() {}

    /**
     * Gets a commit derived from the current staging area.
     * @param message message of the commit
     * @param head SHA1 value of current HEAD
     * @return a commit
     */
    public Commit toCommit(String message, String head) {
        return new Commit(message, new Date(), this.changes, new String[]{head});
    }
}