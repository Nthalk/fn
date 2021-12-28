package com.iodesystems.fn;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeneratorTest {

  @Test
  public void testIterator() {
    // Without caching we lose on repeats
    Fn<Integer> fn = Fn.of(Fn.of(1, 2, 3).iterator());

    assertEquals(fn.size(), 3);
    assertEquals(fn.size(), 0);

    // With caching, we preserve
    fn = Fn.of(Fn.of(1, 2, 3).iterator()).cache();
    assertEquals(fn.size(), 3);
    assertEquals(fn.size(), 3);
  }
}
