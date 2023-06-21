package gitlet;

public class Blob implements GitletObject {
    private byte[] data;
    public Blob(java.io.File f) {
        this.data = Utils.readContents(f);
    }
    public String sha1() {
        return Utils.sha1(this);
    }
}