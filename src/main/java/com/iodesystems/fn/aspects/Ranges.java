package com.iodesystems.fn.aspects;

import java.util.Iterator;

public class Ranges {

  public static Iterable<Integer> of(Integer min, Integer max) {
    return of(min, max, 1);
  }

  public static Iterable<Integer> of(Integer min, Integer max, Integer by) {
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
                this.current += by;
                return tmp;
              }
            };
          }
        },
        i -> i <= max);
  }
}
