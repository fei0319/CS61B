package gitlet;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

/**
 * Represents the staging area and can be converted to a commit.<br>
 * The purpose of implementing GitletObject is to make sure a Stage object can be stored like a GitletObject.
 *
 * @author Fei Pan
 */
public class Staged implements GitletObject {
    /**
     * Used to store staged changes. See Commit.changes for details.
     *
     * @see Commit#tracked
     */
    private HashMap<File, String> changes;

    public HashMap<File, String> getChanges() {
        return changes;
    }

    public String sha1() {
        return Utils.sha1((Object) Utils.serialize(changes));
    }

    /**
     * Add a file to the staging area.
     * If the modified file is identical to what it is in the current commit,
     * remove it from the staging area.
     *
     * @param current current commit
     * @param file    the file to be added
     */
    public void add(Commit current, File file) {
    }

    public void rm(File file) {
    }

    /**
     * Creates an empty staging area object.
     */
    public Staged() {
        changes = new HashMap<>();
    }

    /**
     * Clear the staging area.
     */
    public void clear() {
        changes.clear();
    }
}