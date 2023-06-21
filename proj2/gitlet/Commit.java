package gitlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.io.File;

/** Represents a gitlet commit object.<br>
 *  A Commit object stores message, timestamp, changes and parent commits of a commit.
 *  @author Fei Pan
 */
public class Commit implements GitletObject {

    /** The message of this Commit. */
    private String message;
    /** The timestamp of this Commit */
    private Date date;
    /** The changes contained in this Commit.
     *  <p>
     *  {@code key}: File that was modified<br>
     *  {@code value}: SHA-1 value of blob that stores the content of the file,<br>or null indicating the file is to be deleted
     *  </p>
     */
    private HashMap<File, String> changes;
    /** The parents of this Commit,
     *  which are represented by their SHA-1 values.
     */
    private String[] parents;

    /* TODO: fill in the rest of this class. */

    /**
     * Creates an initial commit.
     * In gitlet, an initial commit is such a commit:
     * message: "initial commit"
     * date: Date(0)
     * changes: empty
     * parents: empty
     */
    public Commit() {
        this("initial commit", new Date(0), new HashMap<>(), new String[0]);
    }
    /**
     * Creates a Commit.
     * @param message message
     * @param date date
     * @param changes changes
     * @param parents SHA-1 values of the parents
     */
    public Commit(String message, Date date, HashMap<File, String> changes, String[] parents) {
        this.message = message;
        this.date = date;
        this.changes = changes;
        this.parents = parents;
    }
    public String sha1() {
        return Utils.sha1((Object) Utils.serialize(this));
    }
    public void show() {
        System.out.println("===");
        System.out.printf("commit %s\n", sha1());
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        System.out.printf("Date: %s\n", formatter.format(date));
        System.out.println(message);
        System.out.println();
    }
    public String[] getParents() {
        return parents;
    }
    public boolean isInitial() {
        return parents.length == 0;
    }
}
