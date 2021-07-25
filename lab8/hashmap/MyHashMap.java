package hashmap;

import java.util.*;

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
    int itemCount;
    int bucketCount;
    double maxLoad;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(16);;
        bucketCount = 16;
        itemCount = 0;
        maxLoad = 0.75;
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        bucketCount = initialSize;
        itemCount = 0;
        maxLoad = 0.75;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        bucketCount = initialSize;
        itemCount = 0;
        this.maxLoad = maxLoad;
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

    // TODO: Implement the methods of the Map61B Interface below
    /** Removes all of the mappings from this map. */
    public void clear() {
        buckets = createTable(16);
        bucketCount = 16;
        itemCount = 0;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        int hash = Math.floorMod(key.hashCode(), bucketCount);
        if (buckets[hash] != null) {
            Iterator nodeIterator = buckets[hash].iterator();
            while (nodeIterator.hasNext()) {
                Node currN = (Node) nodeIterator.next();
                if (currN.key.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        int hash = Math.floorMod(key.hashCode(), bucketCount);
        if (buckets[hash] == null) {
            return null;
        }
        Iterator nodeIterator = buckets[hash].iterator();
        while (nodeIterator.hasNext()) {
            Node currN = (Node) nodeIterator.next();
            if (currN.key.equals(key)) {
                 return currN.value;
            }
        }
        return null;
    }

    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return itemCount;
    }


    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        //if bucket does not exist, create new bucket
        //if bucket does exist, add to bucket if key does not exist, else change curr node
        //buckets[i] returns a Collection of nodes

        if (itemCount / (double) bucketCount > maxLoad) {
            doubleBuckets();
        }
        putBuckets(key, value, buckets);
    }
    private void doubleBuckets() {
        bucketCount *= 2;
        itemCount = 0;
        Collection<Node>[] tempBuckets = createTable(bucketCount);
        for (Collection<Node> n : buckets) {
            /** continue NOT break lol*/
            if (n == null) {
                continue;
            }
            Iterator nodeIterator = n.iterator();
            while (nodeIterator.hasNext()) {
                Node currN = (Node) nodeIterator.next();
                putBuckets(currN.key, currN.value, tempBuckets);
            }
        }
        buckets = tempBuckets;
    }
    private void putBuckets(K key, V value, Collection<Node>[] buckets) {
        int hash = Math.floorMod(key.hashCode(), bucketCount);
        if (buckets[hash] == null) {
            buckets[hash] = createBucket();
        }
        Collection currBucket = buckets[hash];
        Iterator nodeIterator = currBucket.iterator();
        while (nodeIterator.hasNext()) {
            Node currN = (Node) nodeIterator.next();
            if (currN.key.equals(key)) {
                currN.value = value;
                return;
            }
        }
        currBucket.add(createNode(key, value));
        itemCount++;
    }

    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        Set<K> result = new HashSet<>();
        for (Collection currBucket : buckets) {
            if (currBucket != null) {
                Iterator nodeIterator = currBucket.iterator();
                while (nodeIterator.hasNext()) {
                    Node currN = (Node) nodeIterator.next();
                    result.add(currN.key);
                }
            }
        }
        return result;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Iterator iterator() {
        throw new UnsupportedOperationException();
        //return this.keySet().iterator();
    }

    /* private class MyHashMapIterator implements Iterator<K> {
        Set<K> keySet;
        Iterator ksIterator;
        public MyHashMapIterator(MyHashMap hMap) {
            keySet = hMap.keySet();
            Iterator ksIterator = keySet.iterator();
        }
        public boolean hasNext() {
            return ksIterator.hasNext();
        }

        public Node next() {
            if(hasNext()) {
                return new Node(null, null);
            }
        }
    } */

}
