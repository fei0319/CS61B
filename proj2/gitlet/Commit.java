package gitlet;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

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
        return Utils.sha1((Object) Utils.serialize(this));
    }

    /**
     * Show information of this commit in a certain format.
     */
    public void show() {
        Utils.message("===");
        Utils.message("commit %s", sha1());
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        Utils.message("Date: %s", formatter.format(date));
        Utils.message(message);
        Utils.message("");
    }

    /**
     * Append a new parent to the commit.
     *
     * @param parent parent to append
     */
    public void addParent(String parent) {
        List<String> list = new ArrayList<>(List.of(this.parents));
        list.add(parent);
        this.parents = list.toArray(new String[0]);
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

    /**
     * Returns whether this commit is the initial commit.
     * The judgement is done based on the number of parents
     * of the commit. A commit is considered initial iff the
     * number of its parents is 0.
     *
     * @return whether this commit is the initial commit
     */
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

    /**
     * A helper method based on DFS that adds all ancestor commits including
     * self to a specified set.
     *
     * @param commit    commit who and whose ancestors to be added
     * @param ancestors set to add to
     */
    private static void addToAncestors(Commit commit, Set<Commit> ancestors) {
        if (ancestors.contains(commit))
            return;
        ancestors.add(commit);
        for (String next : commit.parents)
            addToAncestors((Commit) GitletObject.read(next), ancestors);
    }

    /**
     * Returns a set contains all ancestors and self of the specified commit.
     *
     * @param commit commit to get ancestors
     * @return a set of ancestors and self
     */
    public static Set<Commit> ancestors(Commit commit) {
        HashSet<Commit> result = new HashSet<>();
        addToAncestors(commit, result);
        return result;
    }

    /**
     * Returns the lowest common ancestor of the
     * specified two commmits.
     *
     * @param a a commit
     * @param b another commit
     * @return the lowest common ancestor
     */
    public static Commit lowestCommonAncestor(Commit a, Commit b) {
        Set<Commit> common = ancestors(a);
        common.retainAll(ancestors(b));

        HashSet<Commit> copy = new HashSet<>(common);
        for (Commit c : copy) {
            for (String next : c.parents) {
                common.remove((Commit) GitletObject.read(next));
            }
        }

        assert common.size() == 1;
        return common.iterator().next();
    }
}
