package com.nthalk.fn;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Fn {

    public static <A, B, C extends Option<B>> Thunk<A, B, C> thunk(final From<A, C> from) {
        return Thunk.of(from);
    }

    public static <T> Iterable<T> unwrap(Iterable<Option<T>> options) {
        return Option.unwrap(options);
    }

    public static <T> Option<T> option(T item) {
        return Option.of(item);
    }

    public static <T> Option<T> empty() {
        return Option.empty();
    }

    public static <V> List<V> filter(Iterable<V> source, Where<V> filter) {
        return Filter.filter(source, filter);
    }

    public static <V> Where<V> not(final V value) {
        return new Where<V>() {
            public boolean is(V v) {
                return !v.equals(value);
            }
        };
    }

    public static <V> Where<V> is(final V value) {
        return new Where<V>() {
            public boolean is(V v) {
                return v.equals(value);
            }
        };
    }

    public static <K, V> Map<K, List<V>> group(Iterable<V> source, From<V, K> extractor) {
        return Grouper.group(source, extractor);
    }

    public static <K, V> Map<K, V> index(Iterable<V> source, From<V, K> extractor) {
        return Indexer.index(source, extractor);
    }

    public static <A> Iterable<A> of(final A... as) {
        return Iterators.of(as);
    }

    public static <A> Iterator<A> take(final int count, final Iterator<A> source) {
        return Iterators.take(count, source);
    }

    public static <A> Iterator<A> drop(int count, final Iterator<A> source) {
        return Iterators.drop(count, source);
    }

    public static <A> Iterable<A> join(final Iterable<A> a, final Iterable<A> b) {
        return Iterators.join(a, b);
    }

    public static <A> Iterable<A> join(final Iterator<A> a, final Iterator<A> b) {
        return Iterators.join(a, b);
    }

    public static <A, B> B reduce(final Iterator<A> source, From<A, B> reducer) {
        return Iterators.reduce(source, reducer);
    }

    public static <A, B> Iterable<B> multiply(A source, final From<A, Iterable<B>> multiplier) {
        return Iterators.multiply(source, multiplier);
    }

    public static <A, B> Iterable<B> multiply(final Iterator<A> sources, final From<A, Iterable<B>> multiplier) {
        return Iterators.multiply(sources, multiplier);
    }

    public static <A, B> Iterable<B> multiply(final Iterable<A> source, final From<A, Iterable<B>> multiplier) {
        return Iterators.multiply(source, multiplier);
    }

    public static <A, B> Iterable<B> from(final Iterable<A> source, final From<A, B> from) {
        return Iterators.from(source, from);
    }
}
