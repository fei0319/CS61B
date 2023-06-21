package gitlet;

import java.io.File;
import java.util.Date;

/** Represents the staging area.
 *  Can be converted to a commit.
 *  @author Fei Pan
 */
public class Stage extends Commit {
    public void add(File file) {}
    public void rm(File file) {}
    public void clear() {}
    public Commit toCommit(String message, String head) {
        return new Commit(message, new Date(), this.changes, new String[]{head});
    }
}