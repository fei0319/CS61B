package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
    private T[] data;
    private int start, size;
    private int next(int pos, int step) {
        return (pos + step) % data.length;
    }
    private int next(int pos) {
        return next(pos, 1);
    }
    private int prev(int pos) {
        return (pos + data.length - 1) % data.length;
    }
    private void resize(int size) {
        T[] newData = (T[]) new Object[size];
        int minSize = Math.min(data.length, size);
        for(int i = 0; i < minSize; ++i) {
            newData[i] = data[start];
            start = next(start);
        }
        data = newData;
        start = 0;
    }
    public ArrayDeque() {
        size = 0;
        data = (T[]) new Object[8];
    }
    @Override
    public void addFirst(T item) {
        if (size == data.length)
            resize(size * 2);
        data[prev(start)] = item;
        start = prev(start);
        ++size;
    }
    @Override
    public void addLast(T item) {
        if (size == data.length)
            resize(size * 2);
        data[next(start, size)] = item;
        ++size;
    }
    @Override
    public T removeFirst() {
        if (size == 0)
            return null;
        T x = data[start];
        data[start] = null;
        start = next(start);
        --size;
        if (data.length > Math.max(size * 4, 8))
            resize(Math.max(size, 8));
        return x;
    }
    @Override
    public T removeLast() {
        if (size == 0)
            return null;
        T x = data[next(start, size - 1)];
        data[next(start, size - 1)] = null;
        --size;
        if (data.length > Math.max(size * 4, 8))
            resize(Math.max(size, 8));
        return x;
    }
    @Override
    public T get(int index) {
        return data[next(start, index)];
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        for (int i = 0, j = start; i < size; ++i, j = next(j))
            System.out.print(data[j] + " ");
        System.out.println();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Deque))
            return false;
        Deque<?> d = (Deque<?>) o;
        if (size() != d.size())
            return false;
        for (int i = 0; i < size; ++i) {
            if (!get(i).equals(d.get(i)))
                return false;
        }
        return true;
    }
    private class DequeIterator implements Iterator<T> {
        private int pos;
        DequeIterator() {
            pos = 0;
        }
        public boolean hasNext() {
            return pos < size();
        }
        public T next() {
            return get(pos++);
        }
    }
    @Override
    public Iterator<T> iterator() {
        return new DequeIterator();
    }
}