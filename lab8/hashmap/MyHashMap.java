package hashmap;

import java.util.*;
import java.util.function.Consumer;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private int initialSize;
    private double maxLoad;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.maxLoad = maxLoad;
        this.size = 0;
        this.buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        size = 0;
        buckets = createTable(initialSize);
    }

    @Override
    public boolean containsKey(K key) {
        Collection<Node> bucket = buckets[Math.floorMod(key.hashCode(), buckets.length)];
        if (bucket == null)
            return false;
        for (Node node : bucket)
            if (node.key.equals(key))
                return true;
        return false;
    }

    @Override
    public V get(K key) {
        Collection<Node> bucket = buckets[Math.floorMod(key.hashCode(), buckets.length)];
        if (bucket == null)
            return null;
        for (Node node : bucket)
            if (node.key.equals(key))
                return node.value;
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private void resize(int tableSize) {
        ArrayList<Node> content = new ArrayList<>();
        for (Collection<Node> bucket : buckets) {
            if (bucket != null)
                for (Node node : bucket)
                    content.add(node);
        }
        buckets = createTable(tableSize);
        size = 0;
        for (Node node : content)
            put(node.key, node.value);
    }

    @Override
    public void put(K key, V value) {
        Collection<Node> bucket = buckets[Math.floorMod(key.hashCode(), buckets.length)];
        if (bucket == null) {
            buckets[Math.floorMod(key.hashCode(), buckets.length)] = createBucket();
            bucket = buckets[Math.floorMod(key.hashCode(), buckets.length)];
        }
        for (Node node : bucket)
            if (node.key.equals(key)) {
                bucket.remove(node);
                --size;
                break;
            }
        bucket.add(new Node(key, value));
        ++size;

        if ((double) size / buckets.length > maxLoad)
            resize(buckets.length * 2);
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> result = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            if (bucket != null)
                for (Node node : bucket)
                    result.add(node.key);
        }
        return result;
    }

    @Override
    public V remove(K key) {
        Collection<Node> bucket = buckets[Math.floorMod(key.hashCode(), buckets.length)];
        if (bucket == null)
            return null;
        for (Node node : bucket)
            if (node.key.equals(key)) {
                V result = node.value;
                bucket.remove(node);
                --size;
                return result;
            }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        Collection<Node> bucket = buckets[Math.floorMod(key.hashCode(), buckets.length)];
        if (bucket == null)
            return null;
        for (Node node : bucket)
            if (node.key.equals(key)) {
                V result = node.value;
                if (!result.equals(value))
                    return null;
                bucket.remove(node);
                --size;
                return result;
            }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

}
