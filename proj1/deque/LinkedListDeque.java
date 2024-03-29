package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {

    private class ItemNode {
        public T data;
        public ItemNode prev, next;
        public ItemNode(T d, ItemNode p, ItemNode n) {
            data = d;
            prev = p;
            next = n;
        }
    }
    private final ItemNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new ItemNode(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        ItemNode newNode = new ItemNode(item, sentinel, sentinel.next);
        newNode.prev.next = newNode;
        newNode.next.prev = newNode;
        ++size;
    }
    @Override
    public void addLast(T item) {
        ItemNode newNode = new ItemNode(item, sentinel.prev, sentinel);
        newNode.prev.next = newNode;
        newNode.next.prev = newNode;
        ++size;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        ItemNode node = sentinel;
        for (int i = 0; i < size; ++i) {
            node = node.next;
            System.out.print(node.data + " ");
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (size == 0)
            return null;
        ItemNode toBeRemoved = sentinel.next;
        toBeRemoved.prev.next = toBeRemoved.next;
        toBeRemoved.next.prev = toBeRemoved.prev;
        --size;
        return toBeRemoved.data;
    }
    @Override
    public T removeLast() {
        if (size == 0)
            return null;
        ItemNode toBeRemoved = sentinel.prev;
        toBeRemoved.prev.next = toBeRemoved.next;
        toBeRemoved.next.prev = toBeRemoved.prev;
        --size;
        return toBeRemoved.data;
    }
    @Override
    public T get(int index) {
        ItemNode node = sentinel.next;
        for (int i = 0; i < index; ++i)
            node = node.next;
        return node.data;
    }
    private T getRecursive(ItemNode node, int index) {
        if (index == 0)
            return node.data;
        else
            return getRecursive(node.next, index - 1);
    }
    public T getRecursive(int index) {
        return getRecursive(sentinel.next, index);
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
        ItemNode node = sentinel;
        for (int i = 0; i < size; ++i) {
            node = node.next;
            if (!node.data.equals(d.get(i)))
                return false;
        }
        return true;
    }
    private class DequeIterator implements Iterator<T> {
        private ItemNode node;
        private int index;
        DequeIterator() {
            node = sentinel;
            index = 0;
        }
        public boolean hasNext() {
            return index < size();
        }
        public T next() {
            node = node.next;
            index += 1;
            return node.data;
        }
    }
    public Iterator<T> iterator() {
        return new DequeIterator();
    }
}