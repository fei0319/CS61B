package deque;

import java.sql.Array;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comp;
    public MaxArrayDeque(Comparator<T> c) {
        comp = c;
    }
    public T max(Comparator<T> c) {
        T result = null;
        for (int i = 0; i < size(); ++i)
            if (result == null || c.compare(get(i), result) > 0)
                result = get(i);
        return result;
    }

    public T max() {
        return max(comp);
    }

}