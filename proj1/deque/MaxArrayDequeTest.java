package deque;

import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.Random;

public class MaxArrayDequeTest {
    private static final int SCALE = 1020;
    private static final int RANGE = 100, LENGTH = 3;
    private static final Random rand = new Random();
    @Test
    public void integerTest() {
        Integer[] a = new Integer[SCALE];
        for (int i = 0; i < SCALE; ++i)
            a[i] = rand.nextInt(RANGE);

        Comparator<Integer> comp = Integer::compare;
        MaxArrayDeque<Integer> d = new MaxArrayDeque<>(comp);
        for (int i = 0; i < SCALE; ++i)
            d.addLast(a[i]);
        Integer max = d.max();
        for (int i = 0; i < SCALE; ++i)
            Assert.assertTrue(comp.compare(max, a[i]) >= 0);
    }
    @Test
    public void stringTest() {
        String[] a = new String[SCALE];
        for (int i = 0; i < SCALE; ++i) {
            String s = "";
            for (int j = 0; j < LENGTH; ++j)
                s += (char)(rand.nextInt(26) + 'a');
            a[i] = s;
        }

        Comparator<String> comp = CharSequence::compare;
        MaxArrayDeque<String> d = new MaxArrayDeque<>(comp);
        for (int i = 0; i < SCALE; ++i)
            d.addLast(a[i]);
        String max = d.max();
        for (int i = 0; i < SCALE; ++i)
            Assert.assertTrue(comp.compare(max, a[i]) >= 0);
    }
}