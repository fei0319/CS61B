package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;

/**
 * The GitletObject class is intended to mimic the four types of objects in real git.
 * It guarantees that its subclasses are serializable and can be conveniently stored.
 *
 * @author Fei Pan
 */
public interface GitletObject extends Serializable {
    /**
     * Get SHA-1 value of the object.
     *
     * @return SHA-1 value of the object
     */
    String sha1();

    /**
     * Store the object to a path determined by its SHA-1 value,
     * namely {@code <Repository.GITLET_DIR>/objects/<SHA-1[:2]>/<SHA-1[2:]>}.<br>
     * Will return SHA-1 value of the object.
     *
     * @return SHA-1 value of the object
     */
    default String store() {
        File target = getPath(sha1());
        Utils.writeObject(target, this);
        return sha1();
    }

    /**
     * Gets the object with SHA-1 value s. If the specified file
     * does not exist, return null.
     *
     * @param s SHA-1 value
     * @return a gitlet object or null if it does not exist
     * @see GitletObject#readAndDeleteUnused(String)
     */
    static GitletObject read(String s) {
        if (s == null)
            return null;
        File f = getPath(s);
        if (!f.exists())
            return null;
        return Utils.readObject(f, GitletObject.class);
    }

    /**
     * Similar to {@link GitletObject#read} except deletes all Staged
     * objects except the one .gitlet/STAGED points to.
     *
     * @param s SHA-1 value
     * @return a gitlet object
     * @see GitletObject#read(String)
     */
    static GitletObject readAndDeleteUnused(String s) {
        GitletObject object = Utils.readObject(getPath(s), GitletObject.class);
        String staged = Repository.getRef("STAGED");
        for (String objectName : list()) {
            if (!objectName.equals(staged) && read(objectName).getClass().equals(Staged.class))
                delete(objectName);
        }
        return object;
    }

    /**
     * Deletes the specified object.
     *
     * @param s object to delete
     */
    static void delete(String s) {
        getPath(s).delete();
    }

    /**
     * Returns a list of SHA-1 value of all objects.
     *
     * @return a list of SHA-1 value of all objects
     */
    static String[] list() {
        File objectDir = Utils.join(Repository.GITLET_DIR, "objects");
        ArrayList<String> result = new ArrayList<>();
        for (String prefix : objectDir.list()) {
            for (String suffix : Utils.join(objectDir, prefix).list())
                result.add(prefix + suffix);
        }
        return result.toArray(new String[0]);
    }

    /**
     * Returns a list of all objects.
     *
     * @return a list of all objects.
     */
    static GitletObject[] listObjects() {
        ArrayList<GitletObject> result = new ArrayList<>();
        for (String objectName : list())
            result.add(read(objectName));
        return result.toArray(new GitletObject[0]);
    }

    /**
     * Gets the path in which the object will be stored and creates necessary directory along the path.
     *
     * @return the path in which the object stores
     */
    static File getPath(String fullName) {
        String prefix = fullName.substring(0, 2), suffix = fullName.substring(2);
        File targetDir = Utils.join(Repository.GITLET_DIR, "objects", prefix);
        targetDir.mkdirs();
        return Utils.join(targetDir, suffix);
    }

    /**
     * Gets corresponding object name to the specified abbreviation.
     * Return null if no matched object is found.
     *
     * @param objectName abbreviation of object name
     * @return full object name, or null if no object matches the abbreviation
     */
    static String autocomplete(String objectName) {
        for (GitletObject object : listObjects()) {
            if (object.sha1().startsWith(objectName))
                return object.sha1();
        }
        return null;
    }
}