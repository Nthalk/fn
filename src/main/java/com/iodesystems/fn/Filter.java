package com.iodesystems.fn;

public abstract class Filter<V> implements Where<V> {

    public static <V> Iterable<V> filter(Iterable<V> source, Where<V> filter) {
        return Iterables.filter(source, filter);
    }

    public Iterable<V> filter(Iterable<V> source) {
        return filter(source, this);
    }

}
