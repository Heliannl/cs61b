package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Heliannl
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
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private Collection<Node>[] buckets;
    private int numBuckets;
    private double loadFactor;
    private int size;
    private HashSet<K> keys;

    /** Constructors */
    public MyHashMap() {
        this (DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this (initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        if (initialSize < 1 || maxLoad <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.numBuckets = initialSize;
        this.loadFactor = maxLoad;
        size = 0;
        buckets = createTable(initialSize);
        keys = new HashSet<>();
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
    protected Collection<Node> createBucket()
    {
        return new LinkedList<>();
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

    /** Removes all the mappings from this map. */
    @Override
    public void clear() {
        buckets = createTable(DEFAULT_INITIAL_SIZE);
        keys = new HashSet<>();
        size = 0;
    }

    /** Help method. */
    private Node search(K key, Collection<Node>[] b) {
        int h = key.hashCode();
        h = Math.floorMod(h, numBuckets);
        Node returnNode = null;
        if (b[h] == null) {
            returnNode = null;
        } else {
            for (Node nd: b[h]) {
                if (nd.key.equals(key)) {
                    returnNode = nd;
                }
            }
        }
        return returnNode;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        if (keys == null || !keys.contains(key)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key. */
    @Override
    public V get(K key) {
        if (keys == null || !keys.contains(key)) {
            return null;
        }
        Node n = search(key, buckets);
        return n.value;
    }

    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced. */

    private void put(K key, V value, Collection<Node>[] b) {
        Node n = createNode(key, value);
        int h = key.hashCode();
        h = Math.floorMod(h, numBuckets);
        Node target = search(key, b);
        if (target == null) {
            if (b[h] == null) {
                b[h] = createBucket();
            }
            b[h].add(n);
        } else {
            target.value = value;
        }
    }

    private void resize(int capacity) {
        Collection<Node>[] newBuckets = createTable(capacity);
        numBuckets = capacity;
        for (int i = 0; i < numBuckets/2; i++){
            if (buckets[i] == null) {
                continue;
            }
            for (Node n: buckets[i]) {
                put(n.key, n.value, newBuckets);
            }
        }
        buckets = newBuckets;
    }

    @Override
    public void put(K key, V value) {
        keys.add(key);
        Node n = createNode(key, value);
        int h = key.hashCode();
        h = Math.floorMod(h, numBuckets);
        Node target = search(key, buckets);
        if (target == null) {
            if (buckets[h] == null) {
                buckets[h] = createBucket();
            }
            buckets[h].add(n);
            size++;
        } else {
            target.value = value;
        }
        if ((double) size / numBuckets >= loadFactor) {
            resize(numBuckets * 2);
        }
    }

    /** Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        return keys;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        int h = key.hashCode();
        h = Math.floorMod(h, numBuckets);
        Node target = search(key, buckets);
        V returnValue = null;
        if (target != null) {
            returnValue = target.value;
            buckets[h].remove(target);
            keys.remove(key);
        }
        return returnValue;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException. */
    @Override
    public V remove(K key, V value) {
        int h = key.hashCode();
        h = Math.floorMod(h, numBuckets);
        Node target = search(key, buckets);
        V returnValue = null;
        if (target != null && target.value.equals(value)) {
            returnValue = value;
            buckets[h].remove(target);
            keys.remove(key);
        }
        return returnValue;
    }


    /**
     * Returns an Iterator that iterates over the stored keys. */
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
