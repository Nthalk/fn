package com.iodesystems.fn;

import java.util.ArrayList;
import java.util.List;

public abstract class Filter<V> implements Where<V> {

    public static <V> List<V> filter(Iterable<V> source, Where<V> filter) {
        List<V> filtered = new ArrayList<V>();
        for (V v : source) {
            if (filter.is(v)) {
                filtered.add(v);
            }
        }
        return filtered;
    }

    public List<V> filter(Iterable<V> source) {
        return filter(source, this);
    }

}
