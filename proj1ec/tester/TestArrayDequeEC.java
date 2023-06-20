package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    public static final int TEST_COUNT = (int) 1e5;
    public static final int RANGE = 10;
    public static final double CHANCE_ADD = 0.7;
    @Test
    public void randomTest() {
        ArrayDequeSolution<Integer> expected = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> actual = new StudentArrayDeque<>();

        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < TEST_COUNT; ++i) {
            assertEquals(expected.size(), actual.size());
            int size = expected.size();
            boolean operateFront = StdRandom.uniform() <= 0.5;
            if (size == 0 || StdRandom.uniform() <= CHANCE_ADD) {
                Integer x = StdRandom.uniform(RANGE);
                if (operateFront) {
                    expected.addFirst(x);
                    actual.addFirst(x);
                    msg.append(String.format("\naddFirst(%d)", x));
                } else {
                    expected.addLast(x);
                    actual.addLast(x);
                    msg.append(String.format("\naddLast(%d)", x));
                }
            } else {
                Integer ex, ac;
                if (operateFront) {
                    ex = expected.removeFirst();
                    ac = actual.removeFirst();
                    msg.append("\nRemoveFirst()");
                } else {
                    ex = expected.removeLast();
                    ac = actual.removeLast();
                    msg.append("\nRemoveLast()");
                }
                assertEquals(msg.toString(), ex, ac);
            }
        }
    }
}
