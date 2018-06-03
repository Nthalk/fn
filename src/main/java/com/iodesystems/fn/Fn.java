package com.iodesystems.fn;

import com.iodesystems.fn.data.*;
import com.iodesystems.fn.logic.Condition;
import com.iodesystems.fn.logic.Where;
import com.iodesystems.fn.thread.Async;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class Fn<A> implements Iterable<A> {
    public static final From<String, Integer> parseInt = Integer::parseInt;
    private static final Fn<?> EMPTY = new Fn<>(Iterables.empty());
    private final Iterable<A> contents;

    protected Fn(Iterable<A> contents) {
        this.contents = contents;
    }

    public static <A> A ifNull(A thing, A ifNull) {
        if (thing == null) return ifNull;
        return thing;
    }

    public static boolean isBlank(String str) {
        return str == null || str.length() == 0;
    }

    public static Fn<String> lines(String input) throws IOException {
        return lines(new ByteArrayInputStream(input.getBytes()));
    }

    public static Fn<String> lines(InputStream ios) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(ios));
        final String first = reader.readLine();
        return of(() -> new Iterator<String>() {
            String next = first;

            @Override
            public void remove() {

            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public String next() {
                String tmp = next;
                try {
                    next = reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return tmp;
            }
        });
    }

    public static String readFully(InputStream ios) throws IOException {
        char[] buffer = new char[4096];
        BufferedReader reader = new BufferedReader(new InputStreamReader(ios));
        StringBuilder result = new StringBuilder();
        int len;
        while ((len = reader.read(buffer)) != -1) {
            result.append(buffer, 0, len);
        }
        return result.toString();
    }

    public static Fn<String> split(final String target, final String on) {
        return of(() -> new Iterator<String>() {
            int nextIndex = target.indexOf(on);
            int lastIndex = 0;

            @Override
            public boolean hasNext() {
                return lastIndex != -1;
            }

            @Override
            public String next() {
                String tmp;
                if (nextIndex == -1) {
                    tmp = target.substring(lastIndex);
                    lastIndex = -1;
                } else {
                    tmp = target.substring(lastIndex, nextIndex);
                    lastIndex = nextIndex + on.length();
                    nextIndex = target.indexOf(on, lastIndex);
                }
                return tmp;
            }

            @Override
            public void remove() {

            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <A, B> Map<A, B> mapOf(A key, B value, Object... rest) {
        Map<A, B> map = new HashMap<>();
        map.put(key, value);
        key = null;
        for (Object o : rest) {
            if (key == null) {
                key = (A) o;
            } else {
                map.put(key, (B) o);
                key = null;
            }
        }
        return map;
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

    public static <V> List<V> emptyList() {
        return Collections.emptyList();
    }

    public static <V> Where<Option<V>> isPresent() {
        return Option.wherePresent();
    }

    public static <A> Condition<A> condition(Where<A> where) {
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
        return v -> !v.equals(value);
    }

    public static <V> Condition<V> is(final V value) {
        return new Condition<V>() {
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
        return new Fn<>(contents);
    }

    public static <A> Fn<A> of(Enumeration<A> contents) {
        return new Fn<>(Iterables.of(contents));
    }

    public static <A> Fn<A> of(final A as) {
        return of(Iterables.of(as));
    }

    public static <A> Fn<A> of(final A... as) {
        return of(Iterables.of(as));
    }

    public static <A> Fn<A> ofNullable(Iterable<A> contents) {
        if (contents == null) {
            return Fn.empty();
        }
        return of(contents).where(Fn.notNull());
    }

    public static <A> Fn<A> ofNullable(final A as) {
        if (as == null) {
            return Fn.empty();
        }
        return of(as);
    }

    public static <A> Fn<A> ofNullable(final A... as) {
        return of(as).where(Fn.notNull());
    }

    public static <A> Fn<A> with(Generator<A> generator) {
        return Fn.of(Iterables.of(generator));
    }

    public static <A> Fn<A> with(Iterable<A> contents) {
        if (contents instanceof Fn) {
            return (Fn<A>) contents;
        }
        return new Fn<>(contents);
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

    public static <A> Fn<A> ofFlatten(Iterable<? extends Iterable<A>> nexts) {
        return of(flatten(nexts));
    }

    public static <A extends Iterable<B>, B> Iterable<B> flatten(Iterable<A> nexts) {
        return Iterables.join(nexts);
    }

    public static <A> Fn<A> ofJoin(Iterable<Iterable<A>> nexts, final A joiner) {
        return of(join(nexts, joiner));
    }

    public static <A> Iterable<A> join(Iterable<Iterable<A>> nexts, final A joiner) {
        return Iterables.multiply(nexts, as -> Iterables.join(as, of(joiner)));
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

    public static <A> List<A> list() {
        return new ArrayList<>();
    }

    public static <A> List<A> list(A a) {
        ArrayList<A> as = new ArrayList<>(1);
        as.add(a);
        return as;
    }

    public static <A> List<A> list(A a, A... rest) {
        ArrayList<A> as = new ArrayList<>(1 + rest.length);
        as.add(a);
        Collections.addAll(as, rest);
        return as;
    }

    public static <A> Fn<Option<A>> tails(Iterable<Iterable<A>> items) {
        return of(items).convert(as -> last(as));
    }

    public static <A> Option<A> last(Iterable<A> contents) {
        if (contents instanceof List) {
            List<A> list = (List<A>) contents;
            if (list.isEmpty()) {
                return Option.empty();
            } else {
                return Option.of(list.get(list.size() - 1));
            }
        } else {
            A last = null;
            for (A a : contents) {
                if (a != null) {
                    last = a;
                }
            }
            return Option.of(last);
        }
    }

    public static <A, B> Pair<A, B> pair(A a, B b) {
        return Pair.of(a, b);
    }

    public static <A> From<A, String> convertToString() {
        return Object::toString;
    }

    public Fn<List<A>> groupsOf(final int size) {
        return of(() -> {
            final List<A> as = toList();
            return new Iterator<List<A>>() {
                int position = 0;

                @Override
                public boolean hasNext() {
                    return position <= as.size();
                }

                @Override
                public List<A> next() {
                    List<A> next = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        int nextPos = position + i;
                        if (nextPos < as.size()) {
                            next.add(as.get(nextPos));
                        }
                    }
                    position += size;
                    return next;
                }

                @Override
                public void remove() {

                }
            };
        });
    }

    public String join(String glue) {
        StringBuilder out = new StringBuilder();
        for (Object part : this) {
            if (part != null) out.append(part.toString());
            out.append(glue);
        }
        if (out.length() > 0) out.delete(out.length() - glue.length(), out.length());
        return out.toString();
    }

    public Fn<A> reverse() {
        List<A> as = toList();
        Collections.reverse(as);
        return Fn.of(as);
    }

    public Enumeration<A> enumeration() {
        return Iterables.toEnumeration(this);
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

    public <B> Fn<B> where(final Class<B> cls) {
        return only(cls::isInstance).convert(a -> {
            //noinspection unchecked
            return (B) a;
        });
    }

    public Fn<A> where(Where<A> where) {
        return only(where);
    }

    public Fn<A> whereNot(Where<A> where) {
        return except(where);
    }

    public Fn<A> except(Where<A> where) {
        return of(filter(contents, Condition.not(where)));
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

    public A[] toArray(A[] preallocated) {
        return Iterables.toArray(contents, preallocated);
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
                return new Pair<>(i++, a);
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
        return Fn.of(other).filter(a -> set.contains(a));
    }

    public Set<A> toSet() {
        return Iterables.toSet(contents);
    }

    public Fn<A> subtract(Iterable<A> other) {
        final Set<A> ignore = Iterables.toSet(other);
        return filter(a -> !ignore.contains(a));
    }

    public Fn<A> difference(Iterable<A> other) {
        final Set<A> set = toSet();
        final Set<A> setOther = Iterables.toSet(other);
        return filter(a -> !setOther.contains(a)).join(Iterables.filter(other, a -> !set.contains(a)));
    }

    public Fn<A> takeWhile(Where<A> where) {
        return Fn.of(Iterables.takeWhile(contents, where));
    }

    public Fn<A> dropWhile(Where<A> where) {
        return Fn.of(Iterables.dropWhile(contents, where));
    }

    public Fn<Iterable<A>> multiply(final Integer times) {
        return convert(a -> Iterables.repeat(a, times));
    }

    public Iterable<Option<A>> optionally(final Where<A> where) {
        return convert(a -> where.is(a) ? Option.of(a) : Option.empty());
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
        if (contents instanceof List) {
            List<A> contents = (List<A>) this.contents;
            if (contents.isEmpty()) return Option.empty();
            return Option.of(contents.get(contents.size() - 1));
        }
        for (A content : contents) {
            last = content;
        }
        return Option.of(last);
    }

    public <B> Fn<Pair<A, B>> extractPair(final From<A, B> extract) {
        return convert(a -> Pair.of(a, extract.from(a)));
    }

    public <B, C> Fn<Pair<A, C>> extractMatch(final From<A, B> extract,
                                              Iterable<C> against,
                                              From<C, B> extractAgainst) {
        final Map<B, C> index = Fn.of(against).index(extractAgainst);
        return extractPair(extract).convert(abPair -> Pair.of(abPair.getA(), index.get(abPair.getB())));
    }

    public Fn<A> withNext(From<A, A> nextFromCurrent) {
        return of(Iterables.withNext(contents, nextFromCurrent));
    }

    public <B> Fn<B> multiplySizedContents(From<A, Integer> getSize, From2<A, Integer, B> getItem) {
        return of(Iterables.sizedContents(contents, getSize, getItem));
    }
}
