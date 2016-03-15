package com.iodesystems.fn;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

    @Test
    public void testGet() {
        int count = 0;
        try {
            Option.empty().get();
        } catch (NoSuchElementException e) {
            count++;
        }
        assertEquals(1, count);
    }

    @Test
    public void testGetIfEmpry() {
        assertEquals(1, Option.empty().get(1));
    }

    @Test
    public void testEquals() {
        assertEquals(Option.empty(), Option.empty());
        assertEquals(Option.of(1), Option.of(1));
    }
}
