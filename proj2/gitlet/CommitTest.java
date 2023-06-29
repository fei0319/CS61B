package gitlet;

import org.junit.Assert;
import org.junit.Test;

public class CommitTest {
    @Test
    public void testLCA() {
        Commit commit = new Commit();
        Commit a = commit.nextCommit("A", new Staged());
        Commit b = a.nextCommit("B", new Staged());
        Commit c = a.nextCommit("C", new Staged());
        a.store();
        b.store();
        c.store();
        commit.store();
        Assert.assertEquals(3, Commit.ancestors(b).size());
        Assert.assertTrue(Commit.ancestors(b).contains(b));
        Assert.assertTrue(Commit.ancestors(b).contains(a));
        Assert.assertTrue(Commit.ancestors(b).contains(commit));

        Assert.assertEquals(Commit.lowestCommonAncestor(b, c), a);
    }
}

