package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.Generator;
import com.iodesystems.fn.logic.Where;
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
        new Where<Integer>() {
          @Override
          public boolean is(Integer i) {
            return i <= max;
          }
        });
  }

  public static Iterable<Integer> of(Integer min, Integer max, Integer by) {
    final int[] current = {min};
    return Iterables.takeWhile(
        Iterables.of(
            new Generator<Integer>() {
              @Override
              public Integer next() {
                final int tmp = current[0];
                current[0] += by;
                return tmp;
              }
            }),
        new Where<Integer>() {
          @Override
          public boolean is(Integer i) {
            return i <= max;
          }
        });
  }
}
