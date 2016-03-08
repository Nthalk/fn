package com.nthalk.fn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class Fn<A> implements Iterable<A> {
    private final Iterable<A> contents;

    public Fn(Iterable<A> contents) {
        this.contents = contents;
    }

    public static <A> Async<A> async(Callable<A> initial) {
        return Async.async(initial);
    }

    public static <A> Async<A> async(Executor executor, Callable<A> initial) {
        return Async.async(executor, initial);
    }

    public static <A> Async<List<A>> await(Executor executor, Async<A>... asyncs) {
        return Async.await(executor, asyncs);
    }

    public static <A> Async.Deferred<A> defer() {
        return Async.defer();
    }

    public static <A> Async.Deferred<A> defer(Executor executor) {
        return Async.defer(executor);
    }

    public static <A> Async<List<A>> await(Async<A>... asyncs) {
        return Async.await(asyncs);
    }

    public static <A, B> Route<A, B> partial(final From<A, Option<B>> from) {
        return Route.of(from);
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

    public static <A> Fn<A> of(final A... as) {
        return new Fn<A>(Iterators.of(as));
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

    public static <A, B> Iterable<B> multiply(A source, final From<A, Iterable<B>> multiplier) {
        return Iterators.multiply(source, multiplier);
    }

    public static <A, B> Iterable<B> multiply(final Iterable<A> source, final From<A, Iterable<B>> multiplier) {
        return Iterators.multiply(source, multiplier);
    }

    public static <A, B> Iterable<B> from(final Iterable<A> source, final From<A, B> from) {
        return Iterators.from(source, from);
    }

    private static <A> Iterable<A> unique(Iterable<A> as) {
        return Iterators.unique(as);
    }

    public <K> Map<K, List<A>> group(From<A, K> extractor) {
        return Fn.group(this, extractor);
    }

    public <B> Map<B, A> index(From<A, B> from) {
        return Fn.index(this, from);
    }

    public Fn<A> join(A... next) {
        return new Fn<A>(Fn.join(this, Fn.of(next)));
    }

    public Fn<A> join(Iterable<A> next) {
        return new Fn<A>(Fn.join(this, next));
    }

    public <B> Fn<B> multiply(From<A, Iterable<B>> multiplier) {
        return new Fn<B>(Fn.multiply(this, multiplier));
    }

    public <B> Fn<B> from(From<A, B> from) {
        return new Fn<B>(Fn.from(this, from));
    }

    public Fn<A> filter(Where<A> where) {
        return new Fn<A>(Fn.filter(this, where));
    }

    @Override
    public Iterator<A> iterator() {
        return contents.iterator();
    }

    public Fn<A> unique() {
        return new Fn<A>(Fn.unique(this));
    }

    public Fn<A> repeat(int times) {
        Fn<A> target = this;
        for (int i = 1; i < times; i++) {
            target = target.join(contents);
        }
        return target;
    }

    public int size() {
        return Iterators.size(this);
    }

    public List<A> toList() {
        List<A> list = new ArrayList<A>();
        for (A content : contents) {
            list.add(content);
        }
        return list;
    }
}
