package deque;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class EqualsTest {
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

        Assert.assertTrue("arr.equals(ll)", arrayDeque.equals(linkedListDeque));
        Assert.assertTrue("ll.equals(arr)", linkedListDeque.equals(arrayDeque));
    }

    @Test
    public void typeTest() {
        Integer[] a = new Integer[SMALL];
        for (int i = 0; i < SMALL; ++i)
            a[i] = rand.nextInt();

        Deque<Integer> arrayDeque = new ArrayDeque<>(), linkedListDeque = new LinkedListDeque<>();
        for (int i = 0; i < SMALL; ++i) {
            arrayDeque.addFirst(a[i]);
            linkedListDeque.addFirst(a[i]);
        }

        Integer integer = 0;
        Assert.assertFalse(arrayDeque.equals(integer));
        Assert.assertFalse(linkedListDeque.equals(integer));
    }

}