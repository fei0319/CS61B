package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> std = new AListNoResizing<>();
        BuggyAList<Integer> tst = new BuggyAList<>();

        std.addLast(3);
        tst.addLast(3);
        std.addLast(6);
        tst.addLast(6);
        std.addLast(1);
        tst.addLast(1);

        assertEquals(std.size(), tst.size());
        int size = std.size();
        for (int i = 0; i < size; ++i)
            assertEquals(std.removeLast(), tst.removeLast());
    }
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> R = new BuggyAList<>();

        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                R.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(), R.size());
                int size = L.size();
            } else if (operationNumber == 2) {
                int size = L.size();
                if (size == 0) {
                    continue;
                }
                assertEquals(L.getLast(), R.getLast());
                assertEquals(L.removeLast(), R.removeLast());
            }
        }
    }
}
