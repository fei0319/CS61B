package gitlet;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Used to test functionality of Blob.
 *
 * @author Fei Pan
 */
public class BlobTest {
    /**
     * Test whether a blob read from a file and another blob
     * read from another file created by the former blob have
     * the same SHA-1 value.
     */
    @Test
    public void testReadWrite() {
        final String FILE_NAME = ".blobtest";
        File f = new File(FILE_NAME), g = new File(FILE_NAME + "1");
        Utils.writeContents(f, "Hello!");

        Blob b = new Blob(f);
        b.saveAs(g);
        Blob c = new Blob(g);
        Assert.assertEquals(b.sha1(), c.sha1());
    }
}