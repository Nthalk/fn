package com.iodesystems.fn;

import com.iodesystems.fn.data.Option;
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
    public void testOrElseIfEmpty() {
        assertEquals(1, Option.empty().orElse(1));
    }

    @Test
    public void testOrResolveIfEmpty() {
        assertEquals(1, Option.empty().orResolve(() -> 1));
    }

    @Test
    public void testGetOrElseIfPresent() {
        assertEquals(Integer.valueOf(1), Option.of(1).orElse(2));
    }

    @Test
    public void testGetOrResolveIfPresent() {
        assertEquals(Integer.valueOf(1), Option.of(1).orResolve(() -> 2
        ));
    }

    @Test
    public void testEquals() {
        assertEquals(Option.empty(), Option.empty());
        assertEquals(Option.of(1), Option.of(1));
    }
}
