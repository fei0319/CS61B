package gitlet;

import java.io.Serializable;
import java.io.File;

public interface GitletObject extends Serializable {
    String sha1();
    default void store(File gitletDir) {
        String fullName = sha1();
        String prefix = fullName.substring(0, 2), suffix = fullName.substring(2);
        File target = Utils.join(gitletDir, prefix, suffix);
        Utils.writeContents(target, this);
    }
}