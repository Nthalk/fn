package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.From;
import java.util.HashMap;
import java.util.Map;

public class Indexes {

  public static <K, V> Map<K, V> index(Iterable<V> source, From<V, K> keyExtractor) {
    Map<K, V> map = new HashMap<>();
    for (V v : source) {
      map.put(keyExtractor.from(v), v);
    }
    return map;
  }

  public static <V, A, K> Map<K, V> index(
      Iterable<A> source, From<A, K> keyExtractor, From<A, V> valueExtractor) {
    Map<K, V> map = new HashMap<>();
    for (A a : source) {
      map.put(keyExtractor.from(a), valueExtractor.from(a));
    }
    return map;
  }
}
