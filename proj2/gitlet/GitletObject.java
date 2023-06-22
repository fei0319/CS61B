package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
     * Gets the object with SHA-1 value s.
     *
     * @param s SHA-1 value
     * @return a gitlet object
     */
    static GitletObject read(String s) {
        return Utils.readObject(getPath(s), GitletObject.class);
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
}