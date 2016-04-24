package com.iodesystems.fn.data;

import java.util.HashMap;
import java.util.Map;

public abstract class Indexer<K, V> implements From<V, K> {

    public static <K, V> Map<K, V> index(Iterable<V> source, From<V, K> extractor) {
        Map<K, V> index = new HashMap<K, V>();
        for (V v : source) {
            index.put(extractor.from(v), v);
        }
        return index;
    }

    public Map<K, V> index(Iterable<V> source) {
        return index(source, this);
    }
}
