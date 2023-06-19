package deque;

import java.util.Iterator;

class LinkedListDeque<T> implements Deque<T> {

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
    public void addFirst(T item) {
        ItemNode newNode = new ItemNode(item, sentinel, sentinel.next);
        newNode.prev.next = newNode;
        newNode.next.prev = newNode;
        ++size;
    }
    public void addLast(T item) {
        ItemNode newNode = new ItemNode(item, sentinel.prev, sentinel);
        newNode.prev.next = newNode;
        newNode.next.prev = newNode;
        ++size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public int size() {
        return size;
    }
    public void printDeque() {
        ItemNode node = sentinel;
        for (int i = 0; i < size; ++i) {
            node = node.next;
            System.out.print(node.data + " ");
        }
        System.out.println();
    }
    public T removeFirst() {
        if (size == 0)
            return null;
        ItemNode toBeRemoved = sentinel.next;
        toBeRemoved.prev.next = toBeRemoved.next;
        toBeRemoved.next.prev = toBeRemoved.prev;
        --size;
        return toBeRemoved.data;
    }
    public T removeLast() {
        if (size == 0)
            return null;
        ItemNode toBeRemoved = sentinel.prev;
        toBeRemoved.prev.next = toBeRemoved.next;
        toBeRemoved.next.prev = toBeRemoved.prev;
        --size;
        return toBeRemoved.data;
    }
    public T get(int index) {
        ItemNode node = sentinel;
        for (int i = 0; i < index; ++i)
            node = node.next;
        return node.data;
    }
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || o.getClass() != this.getClass())
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
            T result = node.data;
            node = node.next;
            index += 1;
            return result;
        }
    }
    public Iterator<T> iterator() {
        return new DequeIterator();
    }
}