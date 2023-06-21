package gitlet;

public class Blob implements GitletObject {
    private byte[] data;
    /**
     * Creates a Blob object from the specified file.
     * @param f file to create Blob from
     */
    public Blob(java.io.File f) {
        this.data = Utils.readContents(f);
    }
    public String sha1() {
        return Utils.sha1(data);
    }
}