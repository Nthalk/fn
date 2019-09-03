package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.Combine;
import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.Generator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Groups {

  public static <A, K, V> Map<K, V> condense(
      Iterable<A> source, From<A, K> keyExtractor, Combine<A, V> combine, V initial) {
    return condense(source, keyExtractor, combine, (Generator<V>) () -> initial);
  }

  public static <A, K, V> Map<K, V> condense(
      Iterable<A> source, From<A, K> keyExtractor, Combine<A, V> combine, Generator<V> initial) {
    Map<K, V> groups = new HashMap<>();
    for (Entry<K, List<A>> entry : group(source, keyExtractor).entrySet()) {
      V value = initial.next();
      for (A a : entry.getValue()) {
        value = combine.from(a, value);
      }
      groups.put(entry.getKey(), value);
    }

    return groups;
  }

  public static <V> Iterable<Iterable<V>> group(Iterable<V> source, Integer groupSize) {
    return () -> {
      Iterator<V> iterator = source.iterator();
      return new Iterator<Iterable<V>>() {

        @Override
        public boolean hasNext() {
          return iterator.hasNext();
        }

        @Override
        public Iterable<V> next() {
          List<V> group = new ArrayList<>(groupSize);
          do {
            group.add(iterator.next());
            if (group.size() == groupSize) {
              return group;
            }
          } while (iterator.hasNext());
          return group;
        }
      };
    };
  }

  public static <A, K, V> Map<K, List<V>> group(
      Iterable<A> source, From<A, K> keyExtractor, From<A, V> valueExtractor) {
    Map<K, List<V>> groups = new HashMap<>();
    if (source != null) {
      for (A a : source) {
        K k = keyExtractor.from(a);
        if (groups.containsKey(k)) {
          groups.get(k).add(valueExtractor.from(a));
        } else {
          List<V> vs = new ArrayList<>();
          vs.add(valueExtractor.from(a));
          groups.put(k, vs);
        }
      }
    }
    return groups;
  }

  public static <K, V> Map<K, List<V>> group(Iterable<V> source, From<V, K> extractor) {
    Map<K, List<V>> groups = new HashMap<>();
    if (source != null) {
      for (V v : source) {
        K k = extractor.from(v);
        if (groups.containsKey(k)) {
          groups.get(k).add(v);
        } else {
          List<V> vs = new ArrayList<>();
          vs.add(v);
          groups.put(k, vs);
        }
      }
    }
    return groups;
  }
}
