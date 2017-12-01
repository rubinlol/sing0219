package com.tencent.appframework.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Tencent.
 * Author: raezlu
 * Date: 12-11-28
 * Time: 下午7:29
 */
public class MultiHashMap<K, V> extends HashMap<K, HashSet<V>> {

    public boolean add(K key, V value) {
        if (value == null) {
            return false;
        }
        HashSet<V> array = get(key);
        if (array == null) {
            array = new HashSet<V>();
            put(key, array);
        }
        return array.add(value);
    }

    public boolean removeSingle(K key, V value) {
        if (value == null) {
            return remove(key) != null;
        } else {
            Collection<V> array = get(key);
            boolean removed = array != null && array.remove(value);
            if (array != null && array.isEmpty()) {
                remove(key);
            }
            return removed;
        }
    }

    public int sizeOf(K key) {
        Collection<V> array = get(key);
        return array == null ? 0 : array.size();
    }
}