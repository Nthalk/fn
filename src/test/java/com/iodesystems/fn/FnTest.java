package com.iodesystems.fn;

import static com.iodesystems.fn.tree.simple.Node.v;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.iodesystems.fn.aspects.Generators;
import com.iodesystems.fn.aspects.Iterables;
import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.Option;
import com.iodesystems.fn.data.Pair;
import com.iodesystems.fn.logic.Where;
import com.iodesystems.fn.tree.simple.Node;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.junit.Test;

public class FnTest {

  A a = new A();
  A2 a2 = new A2();
  Ai aia = new A();
  Ai aia2 = new A2();

  @Test
  public void testPair() {
    assertEquals(Pair.of(1, "a"), Fn.pair(1, "a"));
  }

  @Test
  public void testOfSizedIterable() {
    assertEquals(Fn.list(1, 2, 3), Fn.of(Fn.list(1, 2, 3), List::size, List::get).toList());
  }

  @Test
  public void testOfIterable() {
    assertEquals(Fn.list(1, 2, 3), Fn.of(Iterables.of(1, 2, 3)).toList());
  }

  @Test
  public void testOfVarArgs() {
    assertEquals(Fn.list(1, 2, 3), Fn.of(1, 2, 3).toList());
  }

  @Test
  public void testOfEnumeration() {
    assertEquals(Fn.list(1, 2, 3), Fn.of(Iterables.toEnumeration(Iterables.of(1, 2, 3))).toList());
  }

  @Test
  public void testOfGeneratorConstruction() {
    assertEquals(
        Fn.list(1, 2, 3),
        Fn.of(
                1,
                new From<Integer, Integer>() {
                  @Override
                  public Integer from(Integer i) {
                    return i + 1;
                  }
                })
            .take(3)
            .toList());
  }

  @Test
  public void testOfGenerator() {
    assertEquals(
        Fn.list(1, 2, 3),
        Fn.of(
                Generators.of(
                    1,
                    new From<Integer, Integer>() {
                      @Override
                      public Integer from(Integer i) {
                        return i + 1;
                      }
                    }))
            .take(3)
            .toList());
  }

  @Test
  public void testOfItem() {
    assertEquals(Fn.list(1), Fn.of(1).toList());
  }

  @Test
  public void testCondition() {
    assertTrue(Fn.condition(Objects::nonNull).is(1));
    assertTrue(Fn.condition(Objects::isNull).is(null));
  }

  @Test
  public void testIsNot() {
    assertTrue(Fn.isNot(1).is(2));
  }

  @Test
  public void testIs() {
    assertTrue(Fn.is(1).is(1));
  }

  @Test
  public void testOfPresent() {
    assertEquals(
        Fn.list(1),
        Fn.ofPresent(
                Fn.of(1)
                    .optionally(
                        new Where<Integer>() {
                          @Override
                          public boolean is(Integer i) {
                            return i == 1;
                          }
                        }))
            .toList());
  }

  @Test
  public void testMapOf() {
    assertEquals(
        new HashMap<String, Integer>() {
          {
            put("a", 1);
            put("b", 2);
          }
        },
        Fn.mapOf("a", 1, "b", 2));
  }

  @Test
  public void testPut() {
    HashMap<String, Integer> expected =
        new HashMap<String, Integer>() {
          {
            put("a", 1);
            put("b", 2);
          }
        };
    HashMap<String, Integer> actual =
        new HashMap<String, Integer>() {
          {
            put("a", 1);
          }
        };

    assertEquals(expected, Fn.put(actual, "b", 2));
  }

  @Test
  public void testConvertValues() {
    HashMap<String, Integer> expected =
        new HashMap<String, Integer>() {
          {
            put("a", 1);
            put("b", 2);
          }
        };
    HashMap<String, String> actual =
        new HashMap<String, String>() {
          {
            put("a", "1");
            put("b", "2");
          }
        };
    assertEquals(actual, Fn.convertValues(expected, Object::toString));
  }

  @Test
  public void testConvertKeys() {
    assertEquals(
        Fn.mapOf("1", "1", "2", "2"), Fn.convertKeys(Fn.mapOf(1, "1", 2, "2"), Object::toString));
  }

  @Test
  public void testPutAllMapStatic() {
    assertEquals(
        Fn.mapOf(1, "1", 2, "2"), Fn.putAll(Fn.mapOf(1, "1"), Fn.list("2"), Integer::parseInt));
  }

  @Test
  public void testPutAllMapKeysStatic() {
    assertEquals(
        Fn.mapOf(2, "1", 3, "4"),
        Fn.putAll(
            Fn.mapOf(2, "1"),
            Fn.list("3"),
            Integer::parseInt,
            v -> (Integer.parseInt(v) + 1) + ""));
  }

  @Test
  public void testPutAllMap() {
    assertEquals(Fn.mapOf(1, "1", 2, "2"), Fn.of("2").putAll(Fn.mapOf(1, "1"), Integer::parseInt));
  }

  @Test
  public void testPutAllMapKeys() {
    assertEquals(
        Fn.mapOf(2, "1", 3, "4"),
        Fn.of("3")
            .putAll(Fn.mapOf(2, "1"), Integer::parseInt, v -> (Integer.parseInt(v) + 1) + ""));
  }

  @Test
  public void testGet() {
    assertEquals(Option.of(2), Fn.get(Fn.mapOf("1", 2), "1"));
    assertEquals(Option.empty(), Fn.get(Fn.mapOf("1", 2), "2"));
  }

  @Test
  public void testGetOrAdd() {
    assertEquals(2, Fn.getOrAdd(Fn.mapOf("1", 2), "1", () -> 1L));
    assertEquals(3L, Fn.getOrAdd(Fn.mapOf("1", 2), "2", () -> 3L));
  }

  @Test
  public void testIfNull() {
    assertEquals("a", Fn.ifNull(null, "a"));
    assertEquals("b", Fn.ifNull("b", "a"));
  }

  @Test
  public void testAsync() {
    //    assertEquals(null, Fn.async());
  }

  @Test
  public void testAsync1() {
    //    assertEquals(null, Fn.async1());
  }

  @Test
  public void testAsync2() {
    //    assertEquals(null, Fn.async2());
  }

  @Test
  public void testWhen() {
    //    assertEquals(null, Fn.when());
  }

  @Test
  public void testDefer() {
    //    assertEquals(null, Fn.defer());
  }

  @Test
  public void testDefer1() {
    //    assertEquals(null, Fn.defer1());
  }

  @Test
  public void testWhen1() {
    //    assertEquals(null, Fn.when1());
  }

  @Test
  public void testStackString() {
    try {
      throw new RuntimeException("asdf");
    } catch (Exception e) {
      String s = Fn.stackString(e);
      assertTrue(s.contains("java.lang.RuntimeException: asdf\n"));
      assertTrue(s.contains("at com.iodesystems.fn.FnTest.testStackString"));
    }
  }

  @Test
  public void testRepeat() {
    assertEquals("4444", Fn.repeat(4, 4).join(""));
  }

  @Test
  public void testFlatten() {
    List<List<Integer>> target = Fn.list(Fn.list(1, 2), Fn.list(3, 4), Fn.list(5, 6));
    assertEquals(Fn.range(1, 6).toList(), Fn.flatten(target).toList());
  }

  @Test
  public void testIfBlank() {
    assertEquals("a", Fn.ifBlank("", "a"));
    assertEquals("a", Fn.ifBlank(null, "a"));
    assertEquals("a", Fn.ifBlank("a", "b"));
  }

  @Test
  public void testIsBlank() {
    assertTrue(Fn.isBlank(""));
    assertTrue(Fn.isBlank(null));
    assertFalse(Fn.isBlank("a"));
  }

  @Test
  public void testLinesOfString() {
    assertEquals(Fn.list("1", "2", "3"), Fn.lines(Fn.of(1, 2, 3).join("\n")).toList());
  }

  @Test
  public void testLinesOfInputStream() throws IOException {
    assertEquals(
        Fn.list("1", "2", "3"),
        Fn.lines(new ByteArrayInputStream(Fn.of(1, 2, 3).join("\n").getBytes())).toList());
  }

  @Test
  public void testReadFully() throws IOException {
    String lines = Fn.of(1, 2, 3).join("\n");
    assertEquals(lines, Fn.readFully(new ByteArrayInputStream(lines.getBytes())));
  }

  @Test
  public void testSplit() {
    assertEquals(Fn.list("what", "the", "heck"), Fn.split("what the heck", " ").toList());
  }

  @Test
  public void testNone() {
    assertTrue(Fn.none().isEmpty());
    assertTrue(Fn.none().size() == 0);
  }

  @Test
  public void testRange() {
    assertEquals(Fn.list(1, 2, 3, 4, 5), Fn.range(1, 5).toList());
  }

  @Test
  public void testRangeWithBy() {
    assertEquals(Fn.list(1, 3, 5, 7, 9), Fn.range(1, 10, 2).toList());
  }

  @Test
  public void testList() {
    assertEquals(Arrays.asList(1, 2, 3), Fn.list(1, 2, 3));
  }

  @Test
  public void testIsEqual() {
    assertFalse(Fn.isEqual(1, 2));
    assertTrue(Fn.isEqual(1, 1));
    assertFalse(Fn.isEqual("a", null));
    assertFalse(Fn.isEqual(null, "a"));
    assertTrue(Fn.isEqual(null, null));
  }

  @Test
  public void testWithIndex() {
    assertEquals(
        Fn.list(Fn.pair(0, 1), Fn.pair(1, 2), Fn.pair(2, 3)), Fn.range(1, 3).withIndex().toList());
  }

  @Test
  public void testOptionally() {
    assertEquals(
        Fn.list(Option.empty(), Option.of(2), Option.empty()),
        Fn.of(1, 2, 3).optionally(i -> i % 2 == 0).toList());
  }

  @Test
  public void testToArray() {
    assertArrayEquals(new Integer[] {1, 2, 3}, Fn.of(1, 2, 3).toArray(new Integer[3]));
  }

  @Test
  public void testTakeWhile() {
    assertEquals(Fn.list(1, 2, 3), Fn.range(1, 1000).takeWhile(i -> i < 4).toList());
  }

  @Test
  public void testDropWhile() {
    assertEquals(
        Fn.list(101, 102, 103), Fn.range(1, 1000).dropWhile(i -> i < 101).take(3).toList());
  }

  @Test
  public void testCombine() {
    assertEquals((Object) 7, Fn.of(1, 2, 3).combine(1, (a, b) -> a + b));
  }

  @Test
  public void testWhereClass() {
    A2 a2 = new A2();
    A a = new A();
    assertEquals(Fn.list(a), Fn.of(a).where(Ai.class).toList());
    assertEquals(Fn.list(a2), Fn.of(a, a2).where(A2.class).toList());
    assertEquals(Fn.list(a, a2), Fn.of(a, a2).where(A.class).toList());
  }

  @Test
  public void testWhereValue() {
    assertEquals(Fn.list(2), Fn.of(1, 2, 3).where(2).toList());
  }

  @Test
  public void testNotNull() {
    assertEquals(Fn.list(1, 2), Fn.of(1, null, 2).notNull().toList());
  }

  @Test
  public void testNotValue() {
    assertEquals(Fn.<Ai>list(a, null), Fn.of(a, aia, null).not(aia).toList());
  }

  @Test
  public void testNotClass() {
    assertEquals(Fn.list(null), Fn.of(a, aia, null).not(A.class).toList());
  }

  @Test
  public void testParallel() {
    assertEquals(
        Fn.list(Fn.pair(1, "1"), Fn.pair(2, "2"), Fn.pair(3, "3")),
        Fn.range(1, 3).parallel(Fn.range(1, 3).strings()).toList());
  }

  @Test
  public void testIterator() {
    for (Object o : Fn.of()) {
      assertFalse(true);
    }

    for (Integer i : Fn.of(1)) {
      assertEquals((Integer) 1, i);
    }

    assertTrue(true);
  }

  @Test
  public void testOrElse() {
    assertEquals((Integer) 1, Fn.of(1).orElse(2));
    assertEquals(2, Fn.of().orElse(2));
  }

  @Test
  public void testIsEmpty() {
    assertTrue(Fn.of().isEmpty());
    assertFalse(Fn.of(1).isEmpty());
  }

  @Test
  public void testIsPresent() {
    assertFalse(Fn.of().isPresent());
    assertTrue(Fn.of(1).isPresent());
  }

  @Test
  public void testOrResolve() {
    assertEquals((Integer) 1, Fn.of(1).orResolve(() -> 2));
    assertEquals(2, Fn.of().orResolve(() -> 2));
  }

  @Test
  public void testContents() {
    Iterable<Integer> of = Iterables.of(1, 2, 3);
    assertTrue(of == Fn.of(of).contents());
  }

  @Test
  public void testLoop() {
    assertEquals(Fn.list(1, 2, 3, 1, 2, 3, 1, 2, 3), Fn.of(1, 2, 3).loop(3).toList());
  }

  @Test
  public void testLoopInfinitely() {
    assertEquals(Fn.of(1, 2).loop(25).toList(), Fn.of(1, 2).loop().take(50).toList());
  }

  @Test
  public void testDrop() {
    assertEquals(Fn.list(100), Fn.range(1, 100).drop(99).toList());
  }

  @Test
  public void testLast() {
    assertEquals(Option.of(3), Fn.of(1, 2, 3).last());
    assertEquals(Option.empty(), Fn.of().last());
  }

  @Test
  public void testSplitSets() {
    assertEquals(
        Fn.list(Fn.list(1, 2, 3, 4), Fn.list(5, 6, 7, 8, 9), Fn.list(10)),
        Fn.range(1, 10).split(i -> i % 5 == 0).convert(f -> Fn.of(f).toList()).toList());
  }

  @Test
  public void testLast1() {
    assertEquals(Option.empty(), Fn.<Integer>of().last(i -> i == 2));
    assertEquals(Option.empty(), Fn.of(1).last(i -> i == 2));
    assertEquals(Option.of(3), Fn.of(1, 2, 3).last(i -> i > 1));
  }

  @Test
  public void testFirst() {
    assertEquals(Option.empty(), Fn.<Integer>of().first(i -> i == 2));
    assertEquals(Option.empty(), Fn.of(1).first(i -> i == 2));
    assertEquals(Option.of(2), Fn.of(1, 2, 3).first(i -> i > 1));
  }

  @Test
  public void testFirst1() {
    //    assertEquals(null, Fn.first1());
  }

  @Test
  public void testFirst2() {
    //    assertEquals(null, Fn.first2());
  }

  @Test
  public void testConsume() {
    //    assertEquals(null, Fn.consume());
  }

  @Test
  public void testToList() {
    //    assertEquals(null, Fn.toList());
  }

  @Test
  public void testReverse() {
    assertEquals(Fn.list(3, 2, 1), Fn.of(1, 2, 3).reverse().toList());
  }

  @Test(expected = Success.class)
  public void testToEnumeration() {
    Enumeration<Integer> e = Fn.of(1, 2, 3).toEnumeration();
    assertTrue(e.hasMoreElements());
    assertEquals((Integer) 1, e.nextElement());
    assertEquals((Integer) 2, e.nextElement());
    assertEquals((Integer) 3, e.nextElement());
    assertFalse(e.hasMoreElements());
    try {
      e.nextElement();
      assertFalse(true);
    } catch (NoSuchElementException ignore) {
      throw new Success();
    }
  }

  @Test
  public void testUnique() {
    assertEquals(Fn.list(1, 2, 3), Fn.of(1, 2, 3).loop(30).unique().toList());
  }

  @Test
  public void testConcat() {
    assertEquals(Fn.range(1, 5).toList(), Fn.of(1).concat(Fn.of(2, 3)).concat(4, 5).toList());
  }

  @Test
  public void testEach() {
    final int c[] = {0};
    Fn<Integer> each = Fn.of(1, 2, 3).each(i -> c[0] += i);
    assertEquals(0, c[0]);
    each.consume();
    assertEquals(6, c[0]);
    each.consume();
    assertEquals(12, c[0]);
  }

  @Test
  public void testWithNext() {
    assertEquals(
        Fn.list(1, 2, 3, 4), Fn.of(1).withNext(i -> i > 3 ? null : i + 1).take(10).toList());
  }

  @Test
  public void testTake() {
    assertEquals(Fn.list(1, 2), Fn.of(1, 2, 3).take(2).toList());
  }

  @Test
  public void testToSet() {
    assertEquals(new HashSet<>(Fn.list(1, 2, 3)), Fn.of(1, 2, 3).toSet());
  }

  @Test
  public void testIndex() {
    assertEquals(Fn.mapOf("1", 1, "2", 2, "3", 3), Fn.of(1, 2, 3, 1).index(Object::toString));
  }

  @Test
  public void testBreadth() {
    Node v1 = v(1, v(2, v(3)));
    Node v2 = v1.getChildren().get(0);
    Node v3 = v2.getChildren().get(0);
    assertEquals(Fn.list(v1, v2, v3), Fn.of(v1).breadth(Node::getChildren).toList());
  }

  @Test
  public void testMultiply() {
    //    assertEquals(null, Fn.multiply());
  }

  @Test
  public void testBreadthPaths() {
    Node v1 = v(1, v(2, v(3)));
    Node v2 = v1.getChildren().get(0);
    Node v3 = v2.getChildren().get(0);
    assertEquals(
        Fn.list(Fn.list(v1), Fn.list(v1, v2), Fn.list(v1, v2, v3)),
        Fn.of(v1).breadthPaths(Node::getChildren).toList());
  }

  @Test
  public void testDepth() {
    //    assertEquals(null, Fn.depth());
  }

  @Test
  public void testGroup() {
    assertEquals(
        Fn.list(Fn.range(1, 50).list(), Fn.range(51, 100).list()),
        Fn.range(1, 100).group(50).convert(l -> Fn.of(l).list()).list());
  }

  @Test
  public void testGroup1() {
    assertEquals(
        Fn.mapOf(
            true, Fn.range(1, 50).list(),
            false, Fn.range(51, 100).list()),
        Fn.range(1, 100).group(i -> i <= 50));
  }

  @Test
  public void testSize() {
    assertEquals(3, Fn.of(1, 2, 3).size());
    assertEquals(3, Fn.of(Fn.list(1, 2, 3)).size());
  }

  @Test
  public void testToString() {
    assertEquals("Fn[1, 2]", Fn.of(1, 2).toString());
    assertEquals("Fn[1, 2, 3, 4, 5...]", Fn.of(1, 2, 3, 4, 5, 6).toString());
  }

  @Test
  public void testConvert() {
    assertEquals(Fn.list("1", "2", "3"), Fn.of(1, 2, 3).convert(Objects::toString).toList());
  }

  @Test
  public void testStrings() {
    assertEquals(Fn.list("1", "2"), Fn.of(1, 2).strings().list());
  }

  @Test
  public void testJoin() {
    assertEquals("1 2 3", Fn.of(1, 2, 3).join(" "));
    assertEquals("1,2,3", Fn.of(1, 2, 3).join(","));
  }

  @Test
  public void testPairs() {
    assertEquals(Fn.list(Fn.pair("1", 1)), Fn.of(1).pairs(Object::toString).toList());
  }

  @Test
  public void testPairsWithConversion() {
    assertEquals(Fn.list(Fn.pair("1", 2)), Fn.of(1).pairs(Object::toString, i -> i + 1).toList());
  }

  @Test
  public void testSort() {
    assertEquals(Fn.list(1, 2, 3), Fn.of(3, 2, 1).sort(Integer::compareTo).toList());
  }

  @Test
  public void testSubtract() {
    assertEquals(Fn.of(1, 3).toList(), Fn.of(1, 2, 3).subtract(Fn.of(2)).toList());
  }

  @Test
  public void testIntersection() {
    assertEquals(Fn.of(2).toList(), Fn.of(1, 2, 3).intersection(Fn.of(2)).toList());
  }

  @Test
  public void testDifference() {
    assertEquals(Fn.of(1, 3).toList(), Fn.of(1, 2, 3).difference(Fn.of(2)).toList());
  }

  @Test
  public void testUnion() {
    assertEquals(Fn.of(1, 2, 3, 5).toList(), Fn.of(1, 2, 3).union(Fn.of(5, 3, 2)).toList());
  }

  @Test
  public void testJoinIterable() {
    assertEquals(
        Fn.list(
            Fn.pair(1, 4),
            Fn.pair(1, 5),
            Fn.pair(1, 6),
            Fn.pair(2, 4),
            Fn.pair(2, 5),
            Fn.pair(2, 6),
            Fn.pair(3, 4),
            Fn.pair(3, 5),
            Fn.pair(3, 6)),
        Fn.of(1, 2, 3).join(Fn.of(4, 5, 6)).toList());
  }

  @Test
  public void testJoinOn() {
    assertEquals(
        Fn.list(Fn.pair(1, 4), Fn.pair(2, 5), Fn.pair(3, 6)),
        Fn.of(1, 2, 3).join(i -> i + 3, Fn.of(4, 5, 6)).list());
  }

  @Test
  public void testLeftJoin() {
    assertEquals(
        Fn.list(Fn.pair(1, 4), Fn.pair(2, 5), Fn.pair(3, 6)),
        Fn.of(1, 2, 3, 4).leftJoin(i -> i + 3, Fn.of(4, 5, 6)).list());
  }

  @Test
  public void testJoinOnKV() {
    assertEquals(
        Fn.list(Fn.pair(1, "4"), Fn.pair(2, "5"), Fn.pair(3, "6")),
        Fn.of(1, 2, 3).join(i -> i + 3, Fn.of("4", "5", "6"), Integer::parseInt).list());
  }

  @Test
  public void testLeftJoinOnKV() {
    assertEquals(
        Fn.list(Fn.pair(1, "4"), Fn.pair(2, "5"), Fn.pair(3, "6")),
        Fn.of(1, 2, 3, 4).leftJoin(i -> i + 3, Fn.of("4", "5", "6"), Integer::parseInt).list());
  }

  @Test
  public void testLeftJoin1() {
    //    assertEquals(null, Fn.leftJoin1());
  }

  private interface Ai {}

  private static class A implements Ai {

    @Override
    public String toString() {
      return "A";
    }
  }

  private static class A2 extends A {

    @Override
    public String toString() {
      return "A2";
    }
  }

  private class Success extends RuntimeException {}
}
