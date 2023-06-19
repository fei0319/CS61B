package deque;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class IteratorTest {
    private static final int SMALL = 106;
    private static final Random rand = new Random();
    @Test
    public void randomTest() {
        Integer[] a = new Integer[SMALL];
        for (int i = 0; i < SMALL; ++i)
            a[i] = rand.nextInt();

        Deque<Integer> arrayDeque = new ArrayDeque<>(), linkedListDeque = new LinkedListDeque<>();
        for (int i = 0; i < SMALL; ++i) {
            arrayDeque.addFirst(a[i]);
            linkedListDeque.addFirst(a[i]);
        }

        int pos = 0;
        for (Integer i : arrayDeque) {
            Assert.assertEquals("a failed", arrayDeque.get(pos), i);
            ++pos;
        }
        pos = 0;
        for (Integer i : linkedListDeque) {
            Assert.assertEquals("ll failed", linkedListDeque.get(pos), i);
            ++pos;
        }
    }

}