package gitlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.io.File;
import java.util.Map;

/**
 * Represents a gitlet commit object.<br>
 * A Commit object stores message, timestamp, changes and parent commits of a commit.
 *
 * @author Fei Pan
 */
public class Commit implements GitletObject {

    /**
     * The message of this Commit.
     */
    private String message;
    /**
     * The timestamp of this Commit
     */
    private Date date;
    /**
     * The tracked files contained in this Commit.
     * <p>
     * {@code key}: File that was modified<br>
     * {@code value}: SHA-1 value of blob that stores the content of the file.
     * </p>
     */
    private HashMap<File, String> tracked;
    /**
     * The parents of this Commit,
     * which are represented by their SHA-1 values.
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
     *
     * @param message message
     * @param date    date
     * @param tracked tracked files
     * @param parents SHA-1 values of the parents
     */
    public Commit(String message, Date date, HashMap<File, String> tracked, String[] parents) {
        this.message = message;
        this.date = date;
        this.tracked = tracked;
        this.parents = parents;
    }

    public String sha1() {
        return Utils.sha1(Utils.serialize(this));
    }

    // TODO: Add documentation and replace println with Utils.message
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

    public String getMessage() {
        return message;
    }

    public HashMap<File, String> getTracked() {
        return tracked;
    }

    public boolean isInitial() {
        return parents.length == 0;
    }

    /**
     * Creates a commit from current commit with the specified staging area.
     * The staging area will be cleared afterward.
     *
     * @param message     message for the new commit
     * @param stagingArea staging area to derived commit from
     * @return the derived commit
     */
    public Commit nextCommit(String message, Staged stagingArea) {
        Commit commit = new Commit(message, new Date(), tracked, new String[]{this.sha1()});
        for (Map.Entry<File, String> change : stagingArea.getChanges().entrySet()) {
            if (change.getValue() == null)
                commit.tracked.remove(change.getKey());
            else
                commit.tracked.put(change.getKey(), change.getValue());
        }
        stagingArea.clear();
        return commit;
    }

    /**
     * Returns SHA-1 value of the specified file in this commit.
     * If the file is not tracked, returns null instead.
     *
     * @param f file
     * @return SHA-1 value, or null
     */
    public String getFile(File f) {
        return tracked.get(f);
    }

    /**
     * Returns true if only if the specified file is tracked by this commit.
     *
     * @param f file to check
     * @return true if the file is tracked
     */
    public Boolean hasFile(File f) {
        return tracked.containsKey(f);
    }
}
