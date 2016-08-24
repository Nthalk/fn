package com.iodesystems.fn;

import com.iodesystems.fn.data.*;
import com.iodesystems.fn.logic.Condition;
import com.iodesystems.fn.logic.Where;
import com.iodesystems.fn.thread.Async;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class Fn<A> implements Iterable<A> {
    private static final Fn<?> EMPTY = new Fn<Object>(Iterables.empty());
    private final Iterable<A> contents;

    protected Fn(Iterable<A> contents) {
        this.contents = contents;
    }

    public static <A, B> Option<B> get(Map<A, B> from, A key) {
        if (from.containsKey(key)) {
            return Option.of(from.get(key));
        } else {
            return Option.empty();
        }
    }

    public static <A, B> B getOrAdd(Map<A, B> from, A key, Generator<B> orAdd) {
        if (from.containsKey(key)) {
            return from.get(key);
        } else {
            B add = orAdd.next();
            from.put(key, add);
            return add;
        }
    }

    public static <V> Where<Option<V>> isEmpty() {
        return Option.whereEmpty();
    }

    @SuppressWarnings("unchecked")
    public static <V> Fn<V> empty() {
        return (Fn<V>) EMPTY;
    }

    public static <V> Where<Option<V>> isPresent() {
        return Option.wherePresent();
    }

    public static <A> Condition<A> where(Where<A> where) {
        return Condition.of(where);
    }

    public static <A> Async<A> async(A value) {
        return Async.async(value);
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

    public static <T> Fn<T> ofUnwrap(Iterable<Option<T>> options) {
        return of(unwrap(options));
    }

    public static <V> Fn<V> ofFilter(Iterable<V> source, Where<V> filter) {
        return ofOnly(source, filter);
    }

    public static <V> Fn<V> ofOnly(Iterable<V> source, Where<V> filter) {
        return of(filter(source, filter));
    }

    public static <V> Fn<V> ofExcept(Iterable<V> source, Where<V> filter) {
        return of(except(source, Condition.not(filter)));
    }

    public static <V> Iterable<V> only(Iterable<V> source, Where<V> filter) {
        return Iterables.filter(source, filter);
    }

    public static <V> Iterable<V> except(Iterable<V> source, Where<V> filter) {
        return Iterables.filter(source, Condition.not(filter));
    }

    public static <V> Iterable<V> filter(Iterable<V> source, Where<V> filter) {
        return only(source, filter);
    }

    @SuppressWarnings("unchecked")
    public static <V> Where<V> notNull() {
        return (Where<V>) Where.NOT_NULL;
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

    public static <A> Fn<A> of(Generator<A> generator) {
        return Fn.of(Iterables.of(generator));
    }

    public static <A> Fn<A> of(Iterable<A> contents) {
        if (contents instanceof Fn) {
            return (Fn<A>) contents;
        }
        return new Fn<A>(contents);
    }

    public static <A> Fn<A> of(final A as) {
        return of(Iterables.of(as));
    }

    public static <A> Fn<A> of(final A... as) {
        return of(Iterables.of(as));
    }

    public static <A> Fn<A> with(Generator<A> generator) {
        return Fn.of(Iterables.of(generator));
    }

    public static <A> Fn<A> with(Iterable<A> contents) {
        if (contents instanceof Fn) {
            return (Fn<A>) contents;
        }
        return new Fn<A>(contents);
    }

    public static <A> Fn<A> with(final A as) {
        return of(Iterables.of(as));
    }

    public static <A> Fn<A> with(final A... as) {
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

    public static <A, B> Fn<B> ofConversion(final Iterable<A> source, final From<A, B> from) {
        return of(convert(source, from));
    }

    public static <A, B> Iterable<B> convert(final Iterable<A> source, final From<A, B> from) {
        return Iterables.from(source, from);
    }

    public static <A> Fn<A> ofUnique(Iterable<A> as) {
        return of(unique(as));
    }

    public static <A> Iterable<A> unique(Iterable<A> as) {
        return Iterables.unique(as);
    }

    public static <A> Iterable<A> repeat(Iterable<A> as, int times) {
        return Iterables.repeat(as, times);
    }

    public static <A> Fn<A> ofRepeat(A a, int times) {
        return of(repeat(a, times));
    }

    public static <A> Iterable<A> repeat(A a, int times) {
        return repeat(of(a), times);
    }

    public static <A> Option<A> first(Iterable<A> as, Where<A> is) {
        return Iterables.first(as, is);
    }

    public static <A> A first(Iterable<A> as, Where<A> is, A ifNull) {
        return Iterables.first(as, is).orElse(ifNull);
    }

    public static <A> Option<A> last(Iterable<A> as, Where<A> is) {
        return Iterables.last(as, is);
    }

    public static <A> Fn<A> ofFlatten(Iterable<Iterable<A>> nexts) {
        return of(flatten(nexts));
    }

    public static <A extends Iterable<B>, B> Iterable<B> flatten(Iterable<A> nexts) {
        return Iterables.join(nexts);
    }

    public static <A> Fn<A> ofJoin(Iterable<Iterable<A>> nexts, final A joiner) {
        return of(join(nexts, joiner));
    }

    public static <A> Iterable<A> join(Iterable<Iterable<A>> nexts, final A joiner) {
        return Iterables.multiply(nexts, new From<Iterable<A>, Iterable<A>>() {
            @Override
            public Iterable<A> from(Iterable<A> as) {
                return Iterables.join(as, of(joiner));
            }
        });
    }

    public static Fn<Integer> ofRange(final int start, final int end) {
        return of(range(start, end));
    }

    public static Fn<Integer> ofUntil(final int end) {
        return of(until(end));
    }

    public static Iterable<Integer> until(final int end) {
        return range(0, end);
    }

    public static Iterable<Integer> range(final int start, final int end) {
        return Iterables.of(new Generator<Integer>() {
            int count = 0;

            @Override
            public Integer next() {
                int next = start + count++;
                if (next > end) {
                    return null;
                }
                return next;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> From<T, T> identity() {
        return (From<T, T>) From.IDENTITY;
    }

    public static boolean eq(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    public static <A> List<A> list(A a) {
        ArrayList<A> as = new ArrayList<A>(1);
        as.add(a);
        return as;
    }

    public static <A> List<A> list(A a, A... rest) {
        ArrayList<A> as = new ArrayList<A>(1 + rest.length);
        as.add(a);
        Collections.addAll(as, rest);
        return as;
    }

    public static <A> Fn<A> tails(Fn<List<A>> items) {
        return of(items).convert(new From<List<A>, A>() {
            @Override
            public A from(List<A> as) {
                if (as.isEmpty()) {
                    return null;
                } else {
                    return as.get(as.size() - 1);
                }
            }
        });
    }

    public static <T> From<List<T>, List<T>> list() {
        return new From<List<T>, List<T>>() {
            @Override
            public List<T> from(List<T> ts) {
                return new ArrayList<T>(ts);
            }
        };
    }

    public static <A> Option<A> last(Iterable<A> contents) {
        if (contents instanceof List) {
            // Optimize for list
            List<A> list = (List<A>) contents;
            if (list.isEmpty()) {
                return Option.empty();
            } else {
                return Option.of(list.get(list.size() - 1));
            }
        } else {
            return Fn.of(contents).last();
        }

    }

    public <B> B startWith(B with, Combine<A, B> combine) {
        for (A a : this) {
            with = combine.from(a, with);
        }
        return with;
    }

    public A firstOrElse(A ifNull) {
        return first().orElse(ifNull);
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

    public A first(Where<A> where, A ifNull) {
        return first(contents, where, ifNull);
    }

    public A firstOrNull(Where<A> where) {
        return first(contents, where, null);
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

    public Fn<A> only(Where<A> where) {
        return of(filter(contents, where));
    }

    public Fn<A> except(Where<A> where) {
        return of(filter(contents, not(where)));
    }

    public Condition<A> not(Where<A> where) {
        return Condition.not(where);
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

    public <T> Fn<Pair<T, A>> withIndex(Iterable<T> index) {
        return Fn.of(Iterables.parallel(index, contents));
    }

    public Fn<Pair<Integer, A>> withIndex() {
        return convert(new From<A, Pair<Integer, A>>() {
            int i = 0;

            @Override
            public Pair<Integer, A> from(A a) {
                return new Pair<Integer, A>(i++, a);
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

    public Fn<A> sort(Comparator<A> comparator) {
        List<A> as = toList();
        Collections.sort(as, comparator);
        return Fn.of(as);
    }

    public Fn<A> union(Iterable<A> other) {
        return Fn.of(Iterables.unique(Iterables.join(contents, other)));
    }

    public Fn<A> intersection(Iterable<A> other) {
        final Set<A> set = toSet();
        return Fn.of(other).filter(new Where<A>() {
            @Override
            public boolean is(A a) {
                return set.contains(a);
            }
        });
    }

    public Set<A> toSet() {
        return Iterables.toSet(contents);
    }

    public Fn<A> subtract(Iterable<A> other) {
        final Set<A> ignore = Iterables.toSet(other);
        return filter(new Where<A>() {
            @Override
            public boolean is(A a) {
                return !ignore.contains(a);
            }
        });
    }

    public Fn<A> difference(Iterable<A> other) {
        final Set<A> set = toSet();
        final Set<A> setOther = Iterables.toSet(other);
        return filter(new Where<A>() {
            @Override
            public boolean is(A a) {
                return !setOther.contains(a);
            }
        }).join(Iterables.filter(other, new Where<A>() {
            @Override
            public boolean is(A a) {
                return !set.contains(a);
            }
        }));
    }

    public Fn<A> takeWhile(Where<A> where) {
        return Fn.of(Iterables.takeWhile(contents, where));
    }

    public Fn<A> dropWhile(Where<A> where) {
        return Fn.of(Iterables.dropWhile(contents, where));
    }

    public Fn<Iterable<A>> multiply(final Integer times) {
        return convert(new From<A, Iterable<A>>() {
            @Override
            public Iterable<A> from(A a) {
                return Iterables.repeat(a, times);
            }
        });
    }

    public Iterable<Option<A>> optionally(final Where<A> where) {
        return convert(new From<A, Option<A>>() {
            @Override
            public Option<A> from(A a) {
                return where.is(a) ? Option.of(a) : Option.<A>empty();
            }
        });
    }

    public Fn<A> depth(From<A, Iterable<A>> multiply) {
        return of(Iterables.depth(contents, multiply));
    }

    public Fn<List<A>> breadthPaths(From<A, Iterable<A>> multiply) {
        return of(Iterables.breadthPaths(contents, multiply));
    }

    public Fn<A> breadth(From<A, Iterable<A>> multiply) {
        return of(Iterables.breadth(contents, multiply));
    }

    public Fn<A> loop() {
        return of(Iterables.loop(contents));
    }

    public Option<A> get(int i) {
        return drop(i).first();
    }

    public A getOrElse(int i) {
        return get(i).orElse(null);
    }

    public A getOrElse(int i, A ifEmpty) {
        return get(i).orElse(ifEmpty);
    }

    public Option<A> first() {
        Iterator<A> iterator = contents.iterator();
        if (iterator.hasNext()) {
            return Option.of(iterator.next());
        } else {
            return Option.empty();
        }
    }

    public Option<A> last() {
        A last = null;
        for (A content : contents) {
            last = content;
        }
        return Option.of(last);
    }

}
