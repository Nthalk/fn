package com.iodesystems.fn;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;

public class NextTest {

  @Test
  public void testLengthGetterIterator() {
    List<Integer> source = Fn.ofRange(1, 10).toList();
    SizedIndexAccessor sizedIndexAccessor = new SizedIndexAccessor(source);
    assertEquals(
        source,
        Fn.of(sizedIndexAccessor)
            .multiplySizedContents(SizedIndexAccessor::getSize, SizedIndexAccessor::get)
            .toList());
  }

  @Test
  public void testNextSiblingIterator() {
    HasNext last = null;
    for (Integer value : Fn.range(1, 10)) {
      last = new HasNext(value, last);
    }
    assertEquals(10, Fn.of(last).withNext(HasNext::getNext).size());
  }
}