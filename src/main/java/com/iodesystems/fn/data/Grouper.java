package com.iodesystems.fn.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Grouper<K, V> implements From<V, K> {

    public static <K, V> Map<K, List<V>> group(Iterable<V> source, From<V, K> extractor) {
        Map<K, List<V>> groups = new HashMap<K, List<V>>();
        if (source != null) {
            for (V v : source) {
                K k = extractor.from(v);
                if (groups.containsKey(k)) {
                    groups.get(k).add(v);
                } else {
                    List<V> vs = new ArrayList<V>();
                    vs.add(v);
                    groups.put(k, vs);
                }
            }
        }
        return groups;
    }

    public Map<K, List<V>> group(Iterable<V> source) {
        return group(source, this);
    }
}
