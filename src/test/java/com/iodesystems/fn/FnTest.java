package com.iodesystems.fn;

import static com.iodesystems.fn.Fn.flatten;
import static com.iodesystems.fn.Fn.ifNull;
import static com.iodesystems.fn.Fn.isBlank;
import static com.iodesystems.fn.Fn.ofRange;
import static com.iodesystems.fn.Fn.parseInt;
import static com.iodesystems.fn.Fn.readFully;
import static com.iodesystems.fn.tree.simple.Node.v;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.Generator;
import com.iodesystems.fn.data.Option;
import com.iodesystems.fn.data.Pair;
import com.iodesystems.fn.tree.simple.Node;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class FnTest {

  @Test
  public void testIfNull() {
    assertEquals("a", ifNull(null, "a"));
    assertEquals("a", ifNull("a", "b"));
  }

  @Test
  public void testReadFully() throws IOException {
    assertEquals(
        Fn.ofRange(0, 10000).toList(),
        Fn.lines(readFully(new ByteArrayInputStream(Fn.ofRange(0, 10000).join("\n").getBytes())))
            .convert(parseInt)
            .toList());
  }

  @Test
  public void testIsBlank() {
    assertTrue(isBlank(""));
    //noinspection ConstantConditions
    assertTrue(isBlank(null));
    assertFalse(isBlank("a"));
  }

  @Test
  public void testStackString() {
    try {
      throw new Exception("what");
    } catch (Exception e) {
      assertTrue(Fn.lines(Fn.stackString(e)).size() > 0);
    }
  }

  @Test
  public void testStringSplit() {
    assertEquals(Fn.list("a", "b", "c", ""), Fn.split("a_0b_0c_0", "_0").toList());
    assertEquals(Fn.list(1, 2, 3), Fn.split("1,2,3", ",").convert(Fn.parseInt).toList());

    assertEquals(Fn.list("", "1", "2", "", "3", ""), Fn.split(",1,2,,3,", ",").toList());
  }

  @Test
  public void testStringJoin() {
    assertEquals("-a-1-a-2-a--a-3-a-4-a-", Fn.of(null, 1, 2, null, 3, 4, null).join("-a-"));
  }

  @Test
  public void testGroupsOf() {
    assertEquals(
        Fn.list(Fn.list(0, 1, 2), Fn.list(3, 4, 5), Fn.list(6, 7, 8), Fn.list(9, 10)),
        Fn.ofRange(0, 10).groupsOf(3).toList());
  }

  @Test
  public void testStringSplitAndGroup() {
    Fn.split("a:1,b:2", ",").multiply(s -> Fn.split(s, ":")).groupsOf(3);
  }

  @Test
  public void testWhereClass() {
    A a = new A();
    B b = new B();
    B2 b2 = new B2();
    Fn<Object> of = Fn.of(null, a, b, b2, null);
    assertEquals(Fn.list(b, b2), of.where(B.class).toList());
    assertEquals(Fn.list(a), of.where(A.class).toList());
  }

  @Test
  public void testMap() {
    Map<String, Integer> map = Fn.mapOf("a", 1, "b", 2);
    assertEquals(map.get("a"), (Integer) 1);
    assertEquals(map.get("b"), (Integer) 2);
  }

  @Test
  public void testFlattenNesting() {
    //noinspection ResultOfMethodCallIgnored
    flatten(ofRange(1, 100).multiply(4).convert(integers -> Fn.of(integers).toList()));
  }

  @Test
  public void testDepthMultiply() {
    assertEquals(
        ofRange(1, 8).toList(),
        Fn.of(v(1, v(2, v(3), v(4)), v(5)), v(6, v(7), v(8)))
            .depth(Node::getChildren)
            .convert(Node::getValue)
            .toList());
  }

  @Test
  public void testOptionalFn() {
    assertEquals(Fn.list(), Option.of(null).fn().toList());
    assertEquals(Fn.list(1), Option.of(0).fn().convert(integer -> integer + 1).toList());
  }

  @Test
  public void testBreadthPath() {
    Node v4 = v(4);
    Node v3 = v(3);
    Node v2 = v(2, v4);
    Node v1 = v(1, v2, v3);

    List<List<Node>> paths = Fn.of(v1).breadthPaths(Node.Adapter).toList();
    assertEquals(Fn.of(v1).toList(), paths.get(0));
    assertEquals(Fn.of(v1, v2).toList(), paths.get(1));
    assertEquals(Fn.of(v1, v3).toList(), paths.get(2));
    assertEquals(Fn.of(v1, v2, v4).toList(), paths.get(3));
    assertEquals(4, paths.size());
  }

  @Test
  public void testBreadthPath2() {
    Node v5 = v(5);
    Node v4 = v(4, v5);
    Node v3 = v(3);
    Node v2 = v(2, v3);
    Node v1 = v(1, v2, v4);
    Node v6 = v(6);

    List<List<Node>> paths = Fn.of(v1, v6).breadthPaths(Node.Adapter).toList();
    assertEquals(Fn.of(v1).toList(), paths.get(0));
    assertEquals(Fn.of(v6).toList(), paths.get(1));
    assertEquals(Fn.of(v1, v2).toList(), paths.get(2));
    assertEquals(Fn.of(v1, v4).toList(), paths.get(3));
    assertEquals(Fn.of(v1, v2, v3).toList(), paths.get(4));
    assertEquals(Fn.of(v1, v4, v5).toList(), paths.get(5));

    assertEquals(6, paths.size());
  }

  @Test
  public void testBreadthMultiply() {
    assertEquals(
        ofRange(1, 8).toList(),
        Fn.of(v(1, v(3, v(5), v(6, v(7, v(8)))), v(4)), v(2))
            .breadth(Node::getChildren)
            .convert(Node::getValue)
            .toList());
  }

  @Test
  public void testRepeat() {
    assertEquals(9, Fn.of(1, 2, 3).repeat(3).size());
    assertEquals(3, Fn.of(1).repeat(3).size());
  }

  @Test
  public void testFrom() {
    int size = Fn.of(1, 2, 3, 4).convert(integer -> integer).size();

    assertEquals(4, size);
  }

  @Test
  public void testJoin() {
    assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), Fn.of(1, 2, 3).join(Fn.of(4, 5, 6)).toList());
  }

  @Test
  public void testAndOr() {
    assertEquals(Arrays.asList(2, 4), Fn.of(1, 2, 3, 4).where(Fn.is(2).or(Fn.is(4))).toList());
    assertEquals(
        Collections.singletonList(4),
        Fn.of(1, 2, 3, 4).where(Fn.is(1).or(Fn.is(4)).and(integer -> integer % 2 == 0)).toList());
  }

  @Test
  public void testWithIndex() {
    Map<String, List<Pair<Integer, Integer>>> group =
        Fn.of(1, 2, 3, 4)
            .multiply(integer -> Fn.repeat(integer, integer))
            .withIndex()
            .group(integerIntegerTuple2 -> integerIntegerTuple2.getB().toString());
    assertEquals(4, group.size());
    for (List<Pair<Integer, Integer>> pairs : group.values()) {
      assertEquals(pairs.get(0).getB().intValue(), pairs.size());
    }
  }

  @Test
  public void testEmptyMultiply() {
    assertEquals(
        new ArrayList<Integer>(),
        Fn.<Integer>of().multiply(integer -> Fn.repeat(integer, integer)).toList());
  }

  @Test
  public void testUnwrap() {
    assertEquals(
        Arrays.asList(2, 4),
        Fn.ofUnwrap((Fn.of(1, 2, 3, 4).optionally(integer -> integer % 2 == 0))).toList());
  }

  @Test
  public void testOptionIntegration() {
    Fn<Integer> multiply =
        Fn.of(1, 2, 3, 4)
            // Expensive filter
            .convert(
                (From<Integer, Option<Integer>>)
                    integer -> integer % 2 == 0 ? Option.of(integer) : Option.empty())
            .multiply(integers -> integers);
    assertEquals(2, multiply.size());
  }

  @Test
  public void testIndex() {
    Map<String, Integer> index = Fn.of(1, 2, 3, 4).index(Object::toString);
    assertEquals(new Integer(1), index.get("1"));
  }

  @Test
  public void testSplit() {
    Fn<Iterable<Integer>> split = Fn.of(1, 2, 3, 4, 5).split(integer -> integer % 2 == 0);

    assertEquals(3, split.size());

    Fn<Integer> joined = Fn.ofFlatten(split);
    assertEquals(3, joined.size());
    assertEquals(Option.empty(), joined.first(Fn.is(2)));

    Fn<Integer> joinedWithGlue = Fn.ofJoin(split, 2);
    assertEquals(6, joinedWithGlue.size());
    assertEquals(3, joinedWithGlue.filter(Fn.is(2)).size());

    split = Fn.of(1, 2, 3, 4, 5, 6).split(integer -> integer % 2 == 0);

    assertEquals(4, split.size());
    assertEquals(Option.empty(), joined.first(Fn.is(2)));
    joined = Fn.ofFlatten(split);
    assertEquals(3, joined.size());
    assertEquals(Option.empty(), joined.first(Fn.is(2)));
  }

  @Test
  public void testCombine() {
    Integer combine = Fn.of(1, 2, 3).combine(0, (integer, integer2) -> integer + integer2);
    assertEquals(new Integer(6), combine);
  }

  @Test
  public void testFlatten() {
    Fn<Integer> multiply = Fn.ofFlatten(Fn.of(1).multiply(10));
    assertEquals(10, multiply.size());
  }

  @Test
  public void testMultiply() {
    Fn<Integer> repeat = Fn.of(2).repeat(4);
    assertEquals(4, repeat.size());
    Fn<Iterable<Integer>> multiply = Fn.of(2, 3, 4).multiply(4);
    assertEquals(3, multiply.size());
    Fn<Integer> flatten = Fn.ofFlatten(multiply);
    assertEquals(12, flatten.size());
  }

  @Test
  public void testFn() {
    Map<Integer, List<Integer>> group =
        Fn.of(1, 2, 3)
            .join(4, 8)
            // Strip out odds
            .filter(integer -> integer % 2 == 0)
            // Duplicate list
            .multiply(integer -> Fn.of(integer, integer))
            // Unique list
            .unique()
            // Group by divisible by four
            .group(integer -> integer % 4);

    assertEquals(2, group.size());
    assertEquals(Arrays.asList(4, 8), group.get(0));
    assertEquals(Collections.singletonList(2), group.get(2));
  }

  @Test
  public void testTakeWhile() {
    assertEquals(
        100,
        ofRange(-100, 1000)
            .takeWhile(integer -> integer <= 300)
            .dropWhile(integer -> integer <= 0)
            .filter(integer -> integer % 3 == 0)
            .size());
  }

  @Test
  public void testLast() {
    assertEquals(Option.empty(), Fn.of().last());
  }

  @Test
  public void testLastStatic() {
    assertEquals(Option.empty(), Fn.last(Fn.of()));
  }

  @Test
  public void testGenerate() {
    assertEquals(
        1000,
        Fn.of(
                new Generator<String>() {
                  int i = 0;

                  @Override
                  public String next() {
                    return ((Integer) (i++)).toString();
                  }
                })
            .take(1000)
            .size());
  }

  @Test
  public void testRange() {
    assertEquals(Fn.ofUntil(99).size(), 100);
  }

  @Test
  public void testOfEnumeration() {
    assertEquals(
        Fn.of(Collections.enumeration(Arrays.asList(1, 2, 3))).toList(), Arrays.asList(1, 2, 3));
  }

  @Test
  public void testToListPreallocated() {
    Integer[] preallocated = new Integer[3];
    Integer[] integers = Fn.ofUntil(2).toArray(preallocated);
    assertArrayEquals(integers, new Integer[] {0, 1, 2});
    assertSame("Arrays are the same", integers, preallocated);
  }

  @Test
  public void testNot() {
    assertFalse(Fn.not(4).is(4));
  }

  @Test
  public void testDropWhile() {
    assertEquals(Fn.list(4, 5), Fn.of(1, 2, 3, 4, 5).dropWhile(Fn.not(4)).toList());
  }

  @Test
  public void testToString() {
    Fn<Integer> range = Fn.ofUntil(10);
    assertEquals("Fn[0, 1, 2, 3, 4, ...]", range.toString());
  }

  @Test
  public void testUnion() {
    assertEquals(
        Arrays.asList(1, 2, 3, 4, 5, 6, 7), Fn.of(1, 2, 3, 4, 5).union(Fn.of(4, 5, 6, 7)).toList());
  }

  @Test
  public void testDifference() {
    assertEquals(
        Arrays.asList(1, 2, 3, 6, 7), Fn.of(1, 2, 3, 4, 5).difference(Fn.of(4, 5, 6, 7)).toList());
  }

  @Test
  public void testIntersection() {
    assertEquals(
        Arrays.asList(4, 5), Fn.of(1, 2, 3, 4, 5).intersection(Fn.of(4, 5, 6, 7)).toList());
  }

  @Test
  public void testOfFlatten() {
    List<List<Integer>> lists =
        Fn.of(1, 2, 3).multiply(3).convert(integers -> Fn.of(integers).toList()).toList();
    assertEquals(9, Fn.ofFlatten(lists).size());
  }

  @Test
  public void testPair() {
    assertEquals(new Pair<>(1, "1"), Fn.pair(1, "1"));
  }

  @Test
  public void testMatch() {
    List<Pair<Integer, String>> result =
        Fn.of(1, 2, 3, 4)
            .extractMatch(Fn.convertToString(), Fn.of("1", "2", "3"), Fn.identity())
            .toList();

    assertEquals(
        Fn.list(Fn.pair(1, "1"), Fn.pair(2, "2"), Fn.pair(3, "3"), Fn.pair(4, null)), result);
  }

  @Test
  public void testExtractPair() {
    List<Pair<Integer, String>> extracted = Fn.of(1, 2, 3).extractPair(Object::toString).toList();
    assertEquals(Fn.list(Pair.of(1, "1"), Pair.of(2, "2"), Pair.of(3, "3")), extracted);
  }

  class A {}

  class B {}

  class B2 extends B {}
}
