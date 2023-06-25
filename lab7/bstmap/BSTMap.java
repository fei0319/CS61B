package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A binary search tree implementation for {@link Map61B}.
 *
 * @param <K> type of key
 * @param <V> type of value
 * @author Fei Pan
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    /**
     * A helper class represents tree nodes.
     */
    private class BSTNode {
        public K key;
        public V value;
        public BSTNode left, right;
    }

    /**
     * The root node initially being null.
     */
    private BSTNode root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    private boolean containsKey(BSTNode node, K key) {
        if (node == null)
            return false;
        int c = key.compareTo(node.key);
        if (c < 0)
            return containsKey(node.left, key);
        else if (c > 0)
            return containsKey(node.right, key);
        else
            return true;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private V get(BSTNode node, K key) {
        if (node == null)
            return null;
        int c = key.compareTo(node.key);
        if (c < 0)
            return get(node.left, key);
        else if (c > 0)
            return get(node.right, key);
        else
            return node.value;
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    @Override
    public int size() {
        return this.size;
    }

    BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            node = new BSTNode();
            ++size;
            node.key = key;
        }
        int c = key.compareTo(node.key);
        if (c < 0)
            node.left = put(node.left, key, value);
        else if (c > 0)
            node.right = put(node.right, key, value);
        else
            node.value = value;
        return node;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    void addTo(BSTNode node, Set<K> s) {
        if (node == null)
            return;
        addTo(node.left, s);
        addTo(node.right, s);
        s.add(node.key);
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> result = new HashSet<>();
        addTo(root, result);
        return result;
    }

    BSTNode rotateLeft(BSTNode node) {
        BSTNode root = node.right;
        node.right = node.right.left;
        root.left = node;
        return root;
    }

    BSTNode rotateRight(BSTNode node) {
        BSTNode root = node.left;
        node.left = node.left.right;
        root.right = node;
        return root;
    }

    BSTNode remove(BSTNode node, K key) {
        int c = key.compareTo(node.key);
        if (c == 0) {
            if (node.right != null)
                node = rotateLeft(node);
            else if (node.left != null)
                node = rotateRight(node);
            else return null;
            c = key.compareTo(node.key);
        }
        if (c < 0)
            node.left = remove(node.left, key);
        else
            node.right = remove(node.right, key);
        return node;
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key))
            return null;
        --size;
        V result = get(key);
        root = remove(root, key);
        return result;
    }

    @Override
    public V remove(K key, V value) {
        if (value != null && value.equals(get(key)))
            return remove(key);
        else
            return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}