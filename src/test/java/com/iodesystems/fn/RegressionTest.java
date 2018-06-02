package com.iodesystems.fn;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RegressionTest {
    @Test
    public void notNull() {
        assertTrue(Fn.of((Object) null).only(Fn.notNull()).toList().isEmpty());
    }
}
