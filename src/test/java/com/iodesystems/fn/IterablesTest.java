package com.iodesystems.fn;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class IterablesTest {

  @Test
  public void iterateAllTheThings() {
    Enumeration<Integer> enumeration = Collections.enumeration(Fn.list(1, 2, 3));
    System.out.println("Enumerations!");
    for (Integer value : Fn.of(enumeration)) {
      System.out.print(value);
    }
    System.out.println();

    System.out.println("Sized Buckets!");
    SizedIndexAccessor sizedBucket = new SizedIndexAccessor(Fn.list(1, 2, 3));
    for (Integer value : Fn.of(sizedBucket, SizedIndexAccessor::getSize, SizedIndexAccessor::get)) {
      System.out.print(value);
    }
    System.out.println();

    System.out.println("Next Containers!");
    HasNext linkedItemsRoot = Fn.range(1, 3).reverse().combine(null, HasNext::new);
    for (HasNext hasNext : Fn.of(linkedItemsRoot).withNext(HasNext::getNext)) {
      System.out.print(hasNext.getValue());
    }
    System.out.println();
  }

  @Test
  public void testDrop() {
    assertEquals(Arrays.asList(2, 3), Fn.of(1, 2, 3).drop(1).toList());
  }

  @Test
  public void testRepeat() {
    assertEquals(9, Fn.of(1, 2, 3).loop(3).size());
  }

  @Test
  public void testLoop() {
    assertEquals(100, Fn.of(1).loop().take(100).size());
  }

  @Test
  public void testTake() {
    assertEquals(Arrays.asList(1, 2), Fn.of(1, 2, 3).take(2).toList());
  }

  @Test
  public void testMultiply() {
    Fn<Iterable<Integer>> listsOfIntegers =
        Fn.of(1, 2, 3).multiply(integer -> Fn.of(integer).loop(integer));
    Fn<Integer> multiply = Fn.flatten(listsOfIntegers);

    assertEquals(6, multiply.size());
    assertEquals(3, multiply.unique().size());

    Map<Integer, List<Integer>> group = multiply.group(integer -> integer);

    assertEquals(1, group.get(1).size());
    assertEquals(2, group.get(2).size());
    assertEquals(3, group.get(3).size());
  }
}
