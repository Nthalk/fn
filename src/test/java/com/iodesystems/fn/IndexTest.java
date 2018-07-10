package com.iodesystems.fn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.iodesystems.fn.data.Indexer;
import java.util.Map;
import org.junit.Test;

public class IndexTest {

  @Test
  public void testIndex() {
    Iterable<Integer> source = Fn.of(1, 2, 3, 4, 5);
    Map<String, Integer> index = Indexer.index(source, Object::toString);
    assertEquals(index.size(), 5);
    for (Integer integer : source) {
      assertTrue(index.containsKey(integer.toString()));
    }
  }
}
