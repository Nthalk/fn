package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.Generator;
import com.iodesystems.fn.data.Option;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Maps {

  public static <K, V> Map<K, V> mapOf(Iterator<K> keys, Iterator<V> values) {
    Map<K, V> map = new HashMap<>();
    while (keys.hasNext()) {
      K key = keys.next();
      if (values.hasNext()) {
        V value = values.next();
        map.put(key, value);
      } else {
        break;
      }
    }
    return map;
  }

  public static <K, V> Map<K, V> mapOf(K key, V value, Object... rest) {
    Map<K, V> map = new HashMap<>();
    map.put(key, value);

    Object nextKey = null;
    for (Object nextValue : rest) {
      if (nextKey == null) {
        nextKey = nextValue;
      } else {
        //noinspection unchecked
        map.put((K) nextKey, (V) nextValue);
        nextKey = null;
      }
    }

    return map;
  }

  public static <K, V> Map<K, V> put(Map<K, V> map, K key, V value) {
    map.put(key, value);
    return map;
  }

  public static <K, V, V2> Map<K, V2> convertValues(Map<K, V> map, From<V, V2> valueConverter) {
    Map<K, V2> converted = new HashMap<>();
    for (Entry<K, V> entry : map.entrySet()) {
      converted.put(entry.getKey(), valueConverter.from(entry.getValue()));
    }
    return converted;
  }

  public static <K, K2, V> Map<K2, V> convertKeys(Map<K, V> map, From<K, K2> keyConverter) {
    Map<K2, V> converted = new HashMap<>();
    for (Entry<K, V> entry : map.entrySet()) {
      converted.put(keyConverter.from(entry.getKey()), entry.getValue());
    }
    return converted;
  }

  public static <K, V> Map<K, V> putAll(Map<K, V> map, Iterable<V> items, From<V, K> keyFromItem) {
    for (V item : items) {
      map.put(keyFromItem.from(item), item);
    }

    return map;
  }

  public static <A, K, V> Map<K, V> putAll(
      Map<K, V> map, Iterable<A> items, From<A, K> keyFromItem, From<A, V> valueFromItem) {

    for (A item : items) {
      map.put(keyFromItem.from(item), valueFromItem.from(item));
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

  public static <K, V> Map<K, V> mapOf(Iterable<K> keys, From<K, V> valueExtractor) {
    Map<K, V> map = new HashMap<>();
    for (K key : keys) {
      map.put(key, valueExtractor.from(key));
    }
    return map;
  }
}
