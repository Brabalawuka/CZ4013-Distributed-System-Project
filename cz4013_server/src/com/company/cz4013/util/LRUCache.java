package com.company.cz4013.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> {

    private final LinkedHashMap<K, V> map;
    private final int CAPACITY;
    public LRUCache(int capacity) {
        CAPACITY = capacity;
        map = new LinkedHashMap<>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > CAPACITY;
            }
        };
    }

    public V get(K key) {
        return map.getOrDefault(key, null);
    }

    public void set(K key, V value) {
        map.put(key, value);
    }

}
