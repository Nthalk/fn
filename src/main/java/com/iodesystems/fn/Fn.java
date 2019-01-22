package com.iodesystems.fn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iodesystems.fn.aspects.Exceptions;
import com.iodesystems.fn.aspects.Generators;
import com.iodesystems.fn.aspects.Groups;
import com.iodesystems.fn.aspects.Indexes;
import com.iodesystems.fn.aspects.Iterables;
import com.iodesystems.fn.aspects.Joins;
import com.iodesystems.fn.aspects.Maps;
import com.iodesystems.fn.aspects.Pairs;
import com.iodesystems.fn.aspects.Ranges;
import com.iodesystems.fn.aspects.Sets;
import com.iodesystems.fn.aspects.SizedIterables;
import com.iodesystems.fn.aspects.Strings;
import com.iodesystems.fn.aspects.Trees;
import com.iodesystems.fn.aspects.Values;
import com.iodesystems.fn.aspects.Wheres;
import com.iodesystems.fn.data.Combine;
import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.From2;
import com.iodesystems.fn.data.Generator;
import com.iodesystems.fn.data.Option;
import com.iodesystems.fn.data.Pair;
import com.iodesystems.fn.logic.Condition;
import com.iodesystems.fn.logic.Handler;
import com.iodesystems.fn.logic.Where;
import com.iodesystems.fn.thread.Async;
import com.iodesystems.fn.thread.Deferred;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class Fn<A> extends Option<A> {

  public static final Fn<?> EMPTY = of(Iterables.empty());
  private final Iterable<A> contents;

  public Fn(Iterable<A> contents) {
    this.contents = contents;
  }

  public static <A, B> Pair<A, B> pair(A a, B b) {
    return Pair.of(a, b);
  }

  public static <A, B> Fn<B> of(A source, From<A, Integer> getSize, From2<A, Integer, B> getItem) {
    return of(SizedIterables.of(source, getSize, getItem));
  }

  public static <B> Fn<B> of(Iterable<B> contents) {
    if (contents instanceof Fn) {
      return (Fn<B>) contents;
    }
    return new Fn<>(contents);
  }

  public static <B> Fn<B> of(B contents) {
    return of(Iterables.of(contents));
  }

  @SafeVarargs
  public static <B> Fn<B> of(B... contents) {
    return of(Iterables.of(contents));
  }

  public static <B> Fn<B> of(Enumeration<B> contents) {
    return of(Iterables.of(contents));
  }

  public static <B> Fn<B> of(B initial, From<B, B> next) {
    return of(Generators.of(initial, next));
  }

  public static <B> Fn<B> of(Generator<B> contents) {
    return of(Iterables.of(contents));
  }

  public static <A> Condition<A> condition(Where<A> where) {
    return Condition.of(where);
  }

  public static <A> Condition<A> isNot(A value) {
    return Condition.isNotValue(value);
  }

  public static <A> Condition<A> is(A value) {
    return Condition.isValue(value);
  }

  public static <T> Fn<T> ofPresent(Iterable<Option<T>> options) {
    return of(Option.unwrap(options));
  }

  public static <K, V> Map<K, V> mapOf(K key, V value, Object... rest) {
    return Maps.mapOf(key, value, rest);
  }

  public static <K, V> Map<K, V> mapOf(Iterator<K> keys, Iterator<V> values) {
    return Maps.mapOf(keys, values);
  }

  public static <K> Map<K, K> mapOf(Iterable<K> keys) {
    Iterator<K> iterator = keys.iterator();
    return Maps.mapOf(iterator, iterator);
  }

  public static <K, V> Map<K, V> put(Map<K, V> map, K key, V value) {
    return Maps.put(map, key, value);
  }

  public static <K, V, V2> Map<K, V2> convertValues(Map<K, V> map, From<V, V2> valueConverter) {
    return Maps.convertValues(map, valueConverter);
  }

  public static <K, K2, V> Map<K2, V> convertKeys(Map<K, V> map, From<K, K2> keyConverter) {
    return Maps.convertKeys(map, keyConverter);
  }

  public static <A, K, V> Map<K, V> putAll(
      Map<K, V> map, Iterable<A> items, From<A, K> keyFromItem, From<A, V> valueFromItem) {
    return Maps.putAll(map, items, keyFromItem, valueFromItem);
  }

  public static <K, V> Map<K, V> putAll(Map<K, V> map, Iterable<V> items, From<V, K> keyFromItem) {
    return Maps.putAll(map, items, keyFromItem);
  }

  public static <A, B> Option<B> get(Map<A, B> from, A key) {
    return Maps.get(from, key);
  }

  public static <A, B> B getOrAdd(Map<A, B> from, A key, Generator<B> orAdd) {
    return Maps.getOrAdd(from, key, orAdd);
  }

  public static <A> A ifNull(A value, A ifNull) {
    return Values.ifNull(value, ifNull);
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

  @SafeVarargs
  public static <A> Async<List<A>> when(Executor executor, Async<A>... asyncs) {
    return Async.when(executor, asyncs);
  }

  public static <A> Deferred<A> defer() {
    return Async.defer();
  }

  public static <A> Deferred<A> defer(Executor executor) {
    return Async.defer(executor);
  }

  @SafeVarargs
  public static <A> Async<List<A>> when(Async<A>... asyncs) {
    return Async.when(asyncs);
  }

  public static String stackString(Throwable e) {
    return Exceptions.stackString(e);
  }

  public static <A> Fn<A> repeat(A a, int times) {
    return of(Iterables.repeat(a, times));
  }

  public static <K, V> Map<K, V> index(Iterable<V> contents, From<V, K> keyExtractor) {
    return Indexes.index(contents, keyExtractor);
  }

  public static <A, K, V> Map<K, V> index(
      Iterable<A> contents, From<A, K> keyExtractor, From<A, V> valueExtractor) {
    return Indexes.index(contents, keyExtractor, valueExtractor);
  }

  public static <K, V> Map<K, List<V>> group(Iterable<V> contents, From<V, K> keyExtractor) {
    return Groups.group(contents, keyExtractor);
  }

  public static <A, B extends Iterable<A>> Fn<A> flatten(Iterable<B> contents) {
    return of(Iterables.flatten(contents));
  }

  public static String ifBlank(String thing, String ifBlank) {
    return Strings.ifBlank(thing, ifBlank);
  }

  public static boolean isBlank(String str) {
    return Strings.isBlank(str);
  }

  public static Fn<String> lines(String input) {
    return of(Strings.lines(input));
  }

  public static Fn<String> lines(InputStream input) throws IOException {
    return of(Strings.lines(input));
  }

  public static String readFully(InputStream input) throws IOException {
    return Strings.readFully(input);
  }

  public static Fn<String> split(String input, String on, int limit) {
    return of(Strings.split(input, on, limit));
  }

  public static Fn<String> split(String input, String on) {
    return of(Strings.split(input, on));
  }

  public static Map<String, String> splitMap(
      String input, String groupSeparator, String valueSeparator) {
    return Fn.flatten(Fn.split(input, groupSeparator).convert(s -> Fn.split(s, valueSeparator, 2)))
        .toMap();
  }

  public static <T> Fn<T> none() {
    //noinspection unchecked
    return (Fn<T>) EMPTY;
  }

  public static Fn<Integer> range(int min, int max) {
    return of(Ranges.of(min, max));
  }

  public static Fn<Integer> range(int min, int max, int by) {
    return of(Ranges.of(min, max, by));
  }

  @SafeVarargs
  public static <T> List<T> list(T... ts) {
    if (ts == null) {
      ArrayList<T> objects = new ArrayList<>();
      objects.add(null);
      return objects;
    } else {
      return Arrays.asList(ts);
    }
  }

  public static <A> boolean isEqual(A isThis, A theSameAs) {
    return Values.isEqual(isThis, theSameAs);
  }

  public <K, V> Map<K, V> putAll(Map<K, V> map, From<A, K> keyFromItem, From<A, V> valueFromItem) {
    return Maps.putAll(map, this, keyFromItem, valueFromItem);
  }

  public <K> Map<K, A> putAll(Map<K, A> map, From<A, K> keyFromItem) {
    return Maps.putAll(map, this, keyFromItem);
  }

  public Fn<Pair<Integer, A>> withIndex() {
    return of(Pairs.index(contents));
  }

  public Fn<Option<A>> optionally(Where<A> condition) {
    return convert(
        new From<A, Option<A>>() {
          @Override
          public Option<A> from(A a) {
            return condition.is(a) ? Option.of(a) : Option.empty();
          }
        });
  }

  public A[] toArray(A[] preallocated) {
    return Iterables.toArray(contents, preallocated);
  }

  public Fn<A> takeWhile(Where<A> condition) {
    return of(Iterables.takeWhile(contents, condition));
  }

  public Fn<A> dropWhile(Where<A> condition) {
    return of(Iterables.dropWhile(contents, condition));
  }

  public <B> B combine(B initial, Combine<A, B> condenser) {
    return Iterables.combine(contents, initial, condenser);
  }

  public <B> Fn<B> where(Class<B> cls) {
    //noinspection unchecked
    return of(Iterables.where((Iterable<B>) contents, Wheres.is(cls)));
  }

  public Fn<A> where(Where<A> condition) {
    return of(Iterables.where(contents, condition));
  }

  public Fn<A> where(A value) {
    return of(Iterables.where(contents, Wheres.is(value)));
  }

  public Fn<A> notNull() {
    return of(Iterables.where(contents, Wheres.notNull()));
  }

  public Fn<A> not(A value) {
    return of(Iterables.where(contents, Wheres.not(value)));
  }

  public <B> Fn<A> not(Class<B> cls) {
    //noinspection unchecked
    return of(Iterables.where(contents, (Where<A>) Wheres.not(cls)));
  }

  public Fn<A> not(Where<A> condition) {
    return of(Iterables.where(contents, Wheres.not(condition)));
  }

  public <B> Fn<Pair<A, B>> parallel(Iterable<B> bs) {
    return of(Iterables.parallel(contents, bs));
  }

  @Override
  public Iterator<A> iterator() {
    return contents.iterator();
  }

  @Override
  public A get() {
    return first().get();
  }

  @Override
  public A orElse(A ifEmpty) {
    return first().orElse(ifEmpty);
  }

  @JsonIgnore
  @Override
  public boolean isEmpty() {
    return first().isEmpty();
  }

  @JsonIgnore
  @Override
  public boolean isPresent() {
    return first().isPresent();
  }

  @Override
  public A orResolve(Generator<A> with) {
    return first().orResolve(with);
  }

  public Iterable<A> contents() {
    return contents;
  }

  public Fn<A> loop() {
    return of(Iterables.loop(contents));
  }

  public Fn<A> loop(int times) {
    return of(Iterables.loop(contents, times));
  }

  public Fn<A> drop(final int count) {
    return of(Iterables.drop(count, contents));
  }

  public Option<A> last() {
    return Iterables.last(contents);
  }

  public Fn<Iterable<A>> split(Where<A> splitter) {
    return of(Iterables.split(contents, splitter));
  }

  public Option<A> last(Where<A> condition) {
    return Iterables.last(contents, condition);
  }

  public Option<A> first() {
    return Iterables.first(contents);
  }

  public Option<A> first(Where<A> where) {
    return Iterables.first(contents, where);
  }

  public <B> Option<B> first(Class<B> cls) {
    return Iterables.first(contents, cls);
  }

  public void consume() {
    Iterables.consume(contents);
  }

  public List<A> toList() {
    return Iterables.toList(contents);
  }

  public Fn<A> reverse() {
    List<A> as = toList();
    Collections.reverse(as);
    return of(as);
  }

  public Enumeration<A> toEnumeration() {
    return Iterables.toEnumeration(contents);
  }

  public Fn<A> unique() {
    return of(Iterables.unique(contents));
  }

  @SafeVarargs
  public final Fn<A> concat(A... items) {
    return of(Iterables.concat(contents, Iterables.of(items)));
  }

  public Fn<A> concat(Iterable<A> next) {
    return of(Iterables.concat(contents, next));
  }

  public Fn<A> each(Handler<A> handler) {
    return of(Iterables.each(contents, handler));
  }

  public Fn<A> withNext(From<A, A> nextFromCurrent) {
    return of(Iterables.withNext(contents, nextFromCurrent));
  }

  public Fn<A> take(final int count) {
    return of(Iterables.take(count, contents));
  }

  public Set<A> toSet() {
    return Iterables.toSet(contents);
  }

  public <K> Map<K, A> index(From<A, K> keyExtractor) {
    return Indexes.index(contents, keyExtractor);
  }

  public Fn<A> breadth(final From<A, Iterable<A>> descend) {
    return of(Trees.breadth(contents, descend));
  }

  public <B> Fn<Iterable<B>> multiply(From<A, Iterable<B>> multiplier) {
    return of(Iterables.multiply(contents, multiplier));
  }

  public Fn<List<A>> breadthPaths(final From<A, Iterable<A>> multiplier) {
    return of(Trees.breadthPaths(contents, multiplier));
  }

  public Fn<A> depth(final From<A, Iterable<A>> descend) {
    return of(Trees.depth(contents, descend));
  }

  public Fn<Iterable<A>> group(int size) {
    return of(Groups.group(contents, size));
  }

  public <K> Map<K, List<A>> group(From<A, K> keyExtractor) {
    return Groups.group(contents, keyExtractor);
  }

  public int size() {
    return Iterables.size(contents);
  }

  public List<A> list() {
    return toList();
  }

  @Override
  public String toString() {
    List<A> as = take(5).toList();
    if (as.size() == 5) {
      String s = as.toString();
      return "Fn" + s.substring(0, s.length() - 1) + "...]";
    } else {
      return "Fn" + as.toString();
    }
  }

  public <B> Fn<B> convert(From<A, B> convert) {
    return of(Iterables.convert(contents, convert));
  }

  public Fn<String> strings() {
    return convert(Object::toString);
  }

  public String join(String glue) {
    StringBuilder sb = new StringBuilder();
    for (A content : contents) {
      sb.append(content);
      sb.append(glue);
    }
    int length = sb.length();
    if (length > 0) {
      sb.delete(length - glue.length(), length);
    }
    return sb.toString();
  }

  public <B, C> Fn<Pair<B, C>> pairs(From<A, B> aExtractor, From<A, C> bExtractor) {
    return of(Pairs.pairs(contents, aExtractor, bExtractor));
  }

  public <B, C> Fn<Pair<B, A>> pairs(From<A, B> aExtractor) {
    return of(Pairs.pairs(contents, aExtractor));
  }

  public Fn<A> sort(Comparator<A> comparator) {
    List<A> as = toList();
    //noinspection Java8ListSort
    Collections.sort(as, comparator);
    return of(as);
  }

  public Fn<A> subtract(Iterable<A> these) {
    return of(Sets.subtract(these, contents));
  }

  public Fn<A> intersection(Iterable<A> these) {
    return of(Sets.intersection(these, contents));
  }

  public Fn<A> difference(Iterable<A> these) {
    return of(Sets.difference(contents, these));
  }

  public Fn<A> union(Iterable<A> these) {
    return of(Sets.union(contents, these));
  }

  public <B> Fn<Pair<A, B>> join(Iterable<B> bs) {
    return of(Joins.join(contents, bs));
  }

  public <B> Fn<Pair<A, B>> join(From<A, B> aIndexer, Iterable<B> bs) {
    return of(Joins.join(contents, aIndexer, bs));
  }

  public <B> Fn<Pair<A, B>> leftJoin(From<A, B> aIndexer, Iterable<B> bs) {
    return of(Joins.leftJoin(contents, aIndexer, bs));
  }

  public <B, C> Fn<Pair<A, B>> join(From<A, C> aIndexer, Iterable<B> bs, From<B, C> bIndexer) {
    return of(Joins.join(contents, aIndexer, bs, bIndexer));
  }

  public <B, C> Fn<Pair<A, B>> leftJoin(From<A, C> aIndexer, Iterable<B> bs, From<B, C> bIndexer) {
    return of(Joins.leftJoin(contents, aIndexer, bs, bIndexer));
  }

  public Map<A, A> toMap() {
    return mapOf(this);
  }

  public <V> Map<A, V> toMap(From<A, V> valueExtractor) {
    return Maps.mapOf(this, valueExtractor);
  }
}
