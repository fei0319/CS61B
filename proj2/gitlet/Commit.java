package gitlet;

// TODO: any imports you need here

import javax.sql.rowset.serial.SerialBlob;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.io.File;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements GitletObject {

    /** The message of this Commit. */
    private String message;
    /** The timestamp of this Commit */
    private Date date;
    /** The changes contained in this Commit
     *  key: File that was modified
     *  value: SHA1 value of blob that stores the content of the file
     */
    private HashMap<File, String> changes;
    /** The parents of this Commit,
     *  which are represented by their SHA1 values.
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
     * @param parents SHA1 values of the parents
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
