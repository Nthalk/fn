package com.nthalk.fn;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class OptionTest {

    @Test
    public void testOptions() {
        assertEquals(Option.empty(), Option.of(null));

        for (Object ignore : Option.of(null)) {
            assertTrue(false);
        }

        Option<String> some = Option.of("asdf");
        Iterator<String> iterator = some.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("asdf", iterator.next());
        assertFalse(iterator.hasNext());
    }
}
