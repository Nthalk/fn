package com.iodesystems.fn;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RegressionTest {

  @Test
  public void notNull() {
    assertTrue(Fn.of((Object) null).where(Fn.isNot(null)).toList().isEmpty());
  }
}
