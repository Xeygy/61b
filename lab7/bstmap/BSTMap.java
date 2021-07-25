package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V> {
    int size;
    K key;
    V value;
    BSTMap left;
    BSTMap right;

    public BSTMap(K key, V value) {
        this.key = key;
        this.value = value;
        size = 1;
    }
    public BSTMap() {
        size = 0;
    }
    /** Removes all of the mappings from this map. */
    public void clear() {
        key = null;
        value = null;
        left = null;
        right = null;
        size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return containsHelper(key, this);
    }
    private boolean containsHelper(K key, BSTMap T) {
        if (T == null || T.key == null) {
            return false;
        } else if (T.key.equals(key)) {
            return true;
        } else if (T.key.compareTo(key) > 0) {
            //T.key > key
            return containsHelper(key, T.left);
        } else {
            //T.key < key
            return containsHelper(key, T.right);
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        return getHelper(key, this);
    }
    private V getHelper(K key, BSTMap T) {
        if (T == null || T.key == null) {
            return null;
        } else if (T.key.equals(key)) {
            return (V) T.value;
        } else if (T.key.compareTo(key) > 0) {
            //T.key > key
            return getHelper(key, T.left);
        } else {
            //T.key < key
            return getHelper(key, T.right);
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (this.key == null) {
            size++;
            this.key = key;
            this.value = value;
        } else {
            putHelper(key, value, this);
        }
    }
    private BSTMap putHelper(K key, V value, BSTMap T) {
        if (T == null) {
            size++;
            return new BSTMap(key, value);
        }
        if (T.key.compareTo(key) > 0) {
            //T.key > key
            T.left = putHelper(key, value, T.left);
        } else {
            //T.key < key
            T.right = putHelper(key, value, T.right);
        }
        //T.key == key
        T.value = value;
        return T;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        HashSet<K> ks = new HashSet<>();
        ks.add(keySetHelper(this, ks));
        ks.remove(null);
        return ks;
    }
    private K keySetHelper(BSTMap T, HashSet<K> hs) {
        if (T == null || T.key == null) {
            return null;
        }
        hs.add(keySetHelper(T.left, hs));
        hs.add(keySetHelper(T.right, hs));
        return (K) T.key;
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Iterator<K> iterator() {
        return this.keySet().iterator();
    }


}
