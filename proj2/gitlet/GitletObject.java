package gitlet;

import java.io.Serializable;
import java.io.File;

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
     * namely {@code <gitletDir>/objects/<SHA-1[:2]>/<SHA-1[2:]>}.<br>
     * Will return SHA-1 value of the object.
     *
     * @param gitletDir the {@code .gitlet} directory
     * @return SHA-1 value of the object
     */
    default String store(File gitletDir) {
        File target = getPath(gitletDir, sha1());
        Utils.writeObject(target, this);
        return sha1();
    }

    /**
     * Gets the path in which the object will be stored and creates necessary directory along the path.
     *
     * @param gitletDir the {@code .gitlet} directory
     * @return the path in which the object stores
     */
    static public File getPath(File gitletDir, String fullName) {
        String prefix = fullName.substring(0, 2), suffix = fullName.substring(2);
        File targetDir = Utils.join(gitletDir, "objects", prefix);
        targetDir.mkdirs();
        return Utils.join(targetDir, suffix);
    }

    /**
     * Gets the object with SHA-1 value s.
     *
     * @param gitletDir the {@code .gitlet} directory
     * @param s         SHA-1 value
     * @return a gitlet object
     */
    static public GitletObject read(File gitletDir, String s) {
        return Utils.readObject(getPath(gitletDir, s), GitletObject.class);
    }
}