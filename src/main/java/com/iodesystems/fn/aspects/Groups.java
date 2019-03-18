package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.From;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Groups {

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
