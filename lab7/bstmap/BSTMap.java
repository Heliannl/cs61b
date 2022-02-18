package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;

        BSTNode(K key, V value, BSTNode left, BSTNode right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
        }

        BSTNode get(K k) {
            if (k != null && k.equals(key)) {
                return this;
            }
            if (k.compareTo(key) < 0) {
                if (left == null) {
                    left = new BSTNode(k, null, null, null);
                    return left;
                }
                return left.get(k);
            } else {
                if (right == null) {
                    right = new BSTNode(k, null, null, null);
                    return right;
                }
                return right.get(k);
            }
        }

        BSTNode check(K k) {
            if (k != null && k.equals(key)) {
                return this;
            }
            if (this == null) {
                return null;
            }
            if (k.compareTo(key) < 0) {
                return left.get(k);
            } else {
                return right.get(k);
            }
        }
    }

    private int size;
    private BSTNode bst;

    /** Constructor */
    public BSTMap() {
        size = 0;
        bst = null;
    }

    /** Prints out BSTMao in order of increasing Key. */
    public void printInOrder() {

    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        size = 0;
        bst = null;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        if (bst == null) {
            return false;
        }
        return bst.check(key) != null;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key. */
    @Override
    public V get(K key) {
        if (bst == null) {
            return null;
        }
        BSTNode lookup = bst.check(key);
        if (lookup == null) {
            return null;
        }
        return lookup.value;
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (bst != null) {
            BSTNode lookup = bst.get(key);
            lookup.value = value;

        } else {
            bst = new BSTNode(key, value, null, null);
        }
        size++;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
