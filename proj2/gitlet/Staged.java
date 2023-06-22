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
     * Used to store staged changes.
     * A null key means the specified file is to be removed.
     * See Commit.changes for details.
     *
     * @see Commit#tracked
     */
    private HashMap<File, String> changes;

    /**
     * Set the STAGED ref to self as well as store.
     *
     * @return SHA-1 value of the object
     * @see GitletObject#store
     */
    @Override
    public String store() {
        Repository.setRef("STAGED", sha1());
        return GitletObject.super.store();
    }

    public HashMap<File, String> getChanges() {
        return changes;
    }

    public String sha1() {
        return Utils.sha1((Object) Utils.serialize(changes));
    }

    /**
     * Add a file to the staging area.
     * If the modified file is identical to what it is in the current commit,
     * remove it from the staging area if it is there.
     *
     * @param current current commit
     * @param file    the file to be added
     */
    public void add(Commit current, File file) {
        if (!file.exists())
            Utils.exit("File does not exist.");
        Blob b = new Blob(file);
        if (b.sha1().equals(current.getFile(file))) {
            changes.remove(file);
        } else {
            changes.put(file, b.sha1());
            b.store();
        }
    }

    /**
     * Remove a file from the staging area and return true.
     * If the file is not staged, nothing will happen and return false.
     *
     * @param file file to remove
     * @return true if the file is removed from the staging area
     */
    public boolean rm(File file) {
        if (changes.containsKey(file) && changes.get(file) != null) {
            changes.remove(file);
            return true;
        }
        return false;
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

    /**
     * Returns true if and only if no changes were staged.
     *
     * @return true if there is no changes
     */
    public boolean isEmpty() {
        return changes.isEmpty();
    }

    /**
     * Stage the specified file for removal.
     *
     * @param f file to remove
     */
    public void stageForRemoval(File f) {
        changes.put(f, null);
    }
}