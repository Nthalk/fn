package com.iodesystems.fn.aspects;

public class Ranges {

  public static Iterable<Integer> of(Integer min, Integer max) {
    final int[] current = {min};
    return Iterables.takeWhile(Iterables.of(() -> current[0]++), i -> i <= max);
  }

  public static Iterable<Integer> of(Integer min, Integer max, Integer by) {
    final int[] current = {min};
    return Iterables.takeWhile(Iterables.of(() -> current[0] += by), i -> i <= max);
  }
}
