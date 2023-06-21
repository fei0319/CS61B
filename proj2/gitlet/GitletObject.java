package gitlet;

import java.io.Serializable;
import java.io.File;

/**
 * The GitletObject class is intended to mimic the four types of objects in real git.
 * It guarantees that its subclasses are serializable and can be conveniently stored.
 * @author Fei Pan
 */
public interface GitletObject extends Serializable {
    /**
     * Get SHA1 value of the object.
     * @return SHA1 value of the object
     */
    String sha1();

    /**
     * Store the object to a path determined by its SHA1 value,
     * namely {@code <gitletDir>/objects/<SHA1[:2]>/<SHA1[2:]>}.<br>
     * Will return SHA1 value of the object.
     * @param gitletDir the {@code .gitlet} directory
     * @return SHA1 value of the object
     */
    default String store(File gitletDir) {
        String fullName = sha1();
        String prefix = fullName.substring(0, 2), suffix = fullName.substring(2);
        File targetDir = Utils.join(gitletDir, "objects", prefix);
        File target = Utils.join(targetDir, suffix);
        targetDir.mkdirs();
        Utils.writeObject(target, this);
        return fullName;
    }
}