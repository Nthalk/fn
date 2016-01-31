package com.nthalk.fn;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OptionTest {

    @Test
    public void testOptions() {
        assertEquals(Option.empty(), Option.of(null));
        for (Object ignore : Option.of(null)) {
            Assert.assertTrue(false);
        }
    }
}
