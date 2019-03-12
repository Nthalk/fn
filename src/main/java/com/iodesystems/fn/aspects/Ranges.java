package com.iodesystems.fn.aspects;

import java.util.Iterator;

public class Ranges {

  public static Iterable<Integer> of(Integer min, Integer max) {
    return Iterables.takeWhile(
        new Iterable<Integer>() {
          @Override
          public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {
              int current = min;

              @Override
              public boolean hasNext() {
                return true;
              }

              @Override
              public Integer next() {
                int tmp = this.current;
                this.current++;
                return tmp;
              }
            };
          }
        },
        i -> i <= max);
  }

  public static Iterable<Integer> of(Integer min, Integer max, Integer by) {
    final int[] current = {min};
    return Iterables.takeWhile(
        Iterables.of(
            () -> {
              final int tmp = current[0];
              current[0] += by;
              return tmp;
            }),
        i -> i <= max);
  }
}
