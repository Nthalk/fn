package com.iodesystems.fn;

import com.iodesystems.fn.tuples.Tuple2;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class Fn<A> implements Iterable<A> {
    private final Iterable<A> contents;

    private Fn(Iterable<A> contents) {
        this.contents = contents;
    }

    public static <V> Where<Option<V>> isEmpty() {
        return Option.whereEmpty();
    }

    public static <V> Where<Option<V>> isPresent() {
        return Option.wherePresent();
    }

    public static <A> Condition<A> where(Where<A> where) {
        return Condition.of(where);
    }

    public static <A> Async<A> async(Callable<A> initial) {
        return Async.async(initial);
    }

    public static <A> Async<A> async(Executor executor, Callable<A> initial) {
        return Async.async(executor, initial);
    }

    public static <A> Async<List<A>> when(Executor executor, Async<A>... asyncs) {
        return Async.when(executor, asyncs);
    }

    public static <A> Async.Deferred<A> defer() {
        return Async.defer();
    }

    public static <A> Async.Deferred<A> defer(Executor executor) {
        return Async.defer(executor);
    }

    public static <A> Async<List<A>> when(Async<A>... asyncs) {
        return Async.when(asyncs);
    }

    public static <T> Iterable<T> unwrap(Iterable<Option<T>> options) {
        return Option.unwrap(options);
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

    public static <A> Fn<A> of(Iterable<A> contents) {
        return new Fn<A>(contents);
    }

    public static <A> Fn<A> of(final A... as) {
        return of(Iterables.of(as));
    }

    public static <A> Iterable<A> take(final int count, final Iterable<A> source) {
        return Iterables.take(count, source);
    }

    public static <A> Iterable<A> drop(int count, final Iterable<A> source) {
        return Iterables.drop(count, source);
    }

    public static <A> Iterable<A> join(final Iterable<A> a, final Iterable<A> b) {
        return Iterables.join(a, b);
    }

    public static <A, B> Iterable<B> multiply(final Iterable<A> source, final From<A, Iterable<B>> multiplier) {
        return Iterables.multiply(source, multiplier);
    }

    public static <A, B> Iterable<B> convert(final Iterable<A> source, final From<A, B> from) {
        return Iterables.from(source, from);
    }

    private static <A> Iterable<A> unique(Iterable<A> as) {
        return Iterables.unique(as);
    }

    public static <A> Iterable<A> repeat(Iterable<A> as, int times) {
        return Iterables.repeat(as, times);
    }

    @SuppressWarnings("unchecked")
    public static <A> Iterable<A> repeat(A a, int times) {
        return repeat(of(a), times);
    }

    public static <A> Option<A> first(Iterable<A> as, Where<A> is) {
        return Iterables.first(as, is);
    }

    public static <A> Option<A> last(Iterable<A> as, Where<A> is) {
        return Iterables.last(as, is);
    }

    public static <A> Iterable<A> flatten(Iterable<Iterable<A>> nexts) {
        return of(Iterables.multiply(nexts, new From<Iterable<A>, Iterable<A>>() {
            @Override
            public Iterable<A> from(Iterable<A> as) {
                return as;
            }
        }));
    }

    public static <A> Iterable<A> join(Iterable<Iterable<A>> nexts, final A joiner) {
        return of(Iterables.multiply(nexts, new From<Iterable<A>, Iterable<A>>() {
            @Override
            public Iterable<A> from(Iterable<A> as) {
                return Iterables.join(as, of(joiner));
            }
        }));
    }

    public static Fn<Integer> range(final int start, final int end) {
        return Fn.of(Iterables.generate(new Generator<Integer>() {
            int count = 0;

            @Override
            public Integer next() {
                int next = start + count++;
                if (next > end) {
                    return null;
                }
                return next;
            }
        }));
    }

    public static <T> Fn<T> generate(Generator<T> generator) {
        return Fn.of(Iterables.generate(generator));
    }

    public <K> Map<K, List<A>> group(From<A, K> extractor) {
        return group(contents, extractor);
    }

    public <B> Map<B, A> index(From<A, B> from) {
        return index(contents, from);
    }

    public Fn<Iterable<A>> split(Where<A> splitter) {
        return of(Iterables.split(contents, splitter));
    }

    public Option<A> first(Where<A> where) {
        return first(contents, where);
    }

    public Option<A> last(Where<A> where) {
        return last(contents, where);
    }

    public Fn<A> join(A... next) {
        return of(join(contents, of(next)));
    }

    public Fn<A> join(Iterable<A> next) {
        return of(join(contents, next));
    }

    public <B> Fn<B> multiply(From<A, Iterable<B>> multiplier) {
        return of(multiply(contents, multiplier));
    }

    public <B> Fn<B> convert(From<A, B> from) {
        return of(convert(contents, from));
    }

    public Fn<A> filter(Where<A> where) {
        return of(filter(contents, where));
    }

    public <B> B combine(B initial, Combine<A, B> condenser) {
        return Iterables.combine(contents, initial, condenser);
    }

    @Override
    public Iterator<A> iterator() {
        return contents.iterator();
    }

    public Fn<A> unique() {
        return of(unique(contents));
    }

    public Fn<A> repeat(int times) {
        return of(Iterables.repeat(contents, times));
    }

    public int size() {
        return Iterables.size(contents);
    }

    public List<A> toList() {
        return Iterables.toList(contents);
    }

    public Fn<Tuple2<Integer, A>> withIndex() {
        return convert(new From<A, Tuple2<Integer, A>>() {
            int i = 0;

            @Override
            public Tuple2<Integer, A> from(A a) {
                return new Tuple2<Integer, A>(i++, a);
            }
        });
    }

    public Fn<A> drop(int i) {
        return of(drop(i, contents));
    }

    public Fn<A> take(int i) {
        return of(take(i, contents));
    }

    @Override
    public String toString() {
        List<A> as = take(5).toList();
        if (as.size() == 5) {
            String s = as.toString();
            return "Fn" + s.substring(0, s.length() - 1) + ", ...]";
        } else {
            return "Fn" + as.toString();
        }

    }

    public Fn<A> takeWhile(Where<A> where) {
        return Fn.of(Iterables.takeWhile(contents, where));
    }

    public Fn<A> dropWhile(Where<A> where) {
        return Fn.of(Iterables.dropWhile(contents, where));
    }
}
