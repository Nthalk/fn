package com.iodesystems.fn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.iodesystems.fn.data.Option;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Test;

public class OptionTest {

  @Test
  public void testOptions() {
    assertEquals(Option.empty(), Option.of(null));

    for (Object ignore : Option.of(null)) {
      fail();
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
    assertEquals(Integer.valueOf(1), Option.of(1).orResolve(() -> 2));
  }

  @Test
  public void testEquals() {
    assertEquals(Option.empty(), Option.empty());
    assertEquals(Option.of(1), Option.of(1));
  }
}
