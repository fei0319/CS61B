package gitlet;

import java.io.File;

/**
 * Blob represents binary large object,
 * and can be used to store content of a file.
 * @author Fei Pan
 */
public class Blob implements GitletObject {
    private byte[] data;
    /**
     * Creates a Blob object from the specified file.
     * @param f file to create Blob from
     */
    public Blob(File f) {
        this.data = Utils.readContents(f);
    }

    /**
     * Save the blob to a file.
     * @param f file to save the blob to
     */
    public void saveAs(File f) {
        Utils.writeContents(f, (Object) data);
    }
    public String sha1() {
        return Utils.sha1((Object) data);
    }
}