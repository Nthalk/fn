package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.Generator;
import com.iodesystems.fn.logic.Where;

public class Ranges {

  public static Iterable<Integer> of(Integer min, Integer max) {
    final int[] current = {min};
    return Iterables.takeWhile(
        Iterables.of(
            new Generator<Integer>() {
              @Override
              public Integer next() {
                return current[0]++;
              }
            }),
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
                return current[0] += by;
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
