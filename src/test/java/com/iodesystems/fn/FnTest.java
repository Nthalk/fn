package com.iodesystems.fn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.iodesystems.fn.aspects.Generators;
import com.iodesystems.fn.aspects.Iterables;
import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.Pair;
import com.iodesystems.fn.logic.Where;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.junit.Test;

public class FnTest {

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
    //    assertEquals(null, Fn.convertKeys());
  }

  @Test
  public void testPutAll() {
    //    assertEquals(null, Fn.putAll());
  }

  @Test
  public void testPutAll1() {
    //    assertEquals(null, Fn.putAll1());
  }

  @Test
  public void testGet() {
    //    assertEquals(null, Fn.get());
  }

  @Test
  public void testGetOrAdd() {
    //    assertEquals(null, Fn.getOrAdd());
  }

  @Test
  public void testIfNull() {
    //    assertEquals(null, Fn.ifNull());
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
    //    assertEquals(null, Fn.stackString());
  }

  @Test
  public void testRepeat() {
    //    assertEquals(null, Fn.repeat());
  }

  @Test
  public void testFlatten() {
    //    assertEquals(null, Fn.flatten());
  }

  @Test
  public void testIfBlank() {
    //    assertEquals(null, Fn.ifBlank());
  }

  @Test
  public void testIsBlank() {
    //    assertEquals(null, Fn.isBlank());
  }

  @Test
  public void testLines() {
    //    assertEquals(null, Fn.lines());
  }

  @Test
  public void testLines1() {
    //    assertEquals(null, Fn.lines1());
  }

  @Test
  public void testReadFully() {
    //    assertEquals(null, Fn.readFully());
  }

  @Test
  public void testSplit() {
    //    assertEquals(null, Fn.split());
  }

  @Test
  public void testNone() {
    //    assertEquals(null, Fn.none());
  }

  @Test
  public void testRange() {
    //    assertEquals(null, Fn.range());
  }

  @Test
  public void testRange1() {
    //    assertEquals(null, Fn.range1());
  }

  @Test
  public void testList() {
    //    assertEquals(null, Fn.list());
  }

  @Test
  public void testIsEqual() {
    //    assertEquals(null, Fn.isEqual());
  }

  @Test
  public void testWithIndex() {
    //    assertEquals(null, Fn.withIndex());
  }

  @Test
  public void testOptionally() {
    //    assertEquals(null, Fn.optionally());
  }

  @Test
  public void testToArray() {
    //    assertEquals(null, Fn.toArray());
  }

  @Test
  public void testTakeWhile() {
    //    assertEquals(null, Fn.takeWhile());
  }

  @Test
  public void testDropWhile() {
    //    assertEquals(null, Fn.dropWhile());
  }

  @Test
  public void testCombine() {
    //    assertEquals(null, Fn.combine());
  }

  @Test
  public void testWhere() {
    //    assertEquals(null, Fn.where());
  }

  @Test
  public void testWhere1() {
    //    assertEquals(null, Fn.where1());
  }

  @Test
  public void testNotNull() {
    //    assertEquals(null, Fn.notNull());
  }

  @Test
  public void testNot() {
    //    assertEquals(null, Fn.not());
  }

  @Test
  public void testNot1() {
    //    assertEquals(null, Fn.not1());
  }

  @Test
  public void testParallel() {
    //    assertEquals(null, Fn.parallel());
  }

  @Test
  public void testIterator() {
    //    assertEquals(null, Fn.iterator());
  }

  @Test
  public void testGet1() {
    //    assertEquals(null, Fn.get1());
  }

  @Test
  public void testOrElse() {
    //    assertEquals(null, Fn.orElse());
  }

  @Test
  public void testIsEmpty() {
    //    assertEquals(null, Fn.isEmpty());
  }

  @Test
  public void testIsPresent() {
    //    assertEquals(null, Fn.isPresent());
  }

  @Test
  public void testOrResolve() {
    //    assertEquals(null, Fn.orResolve());
  }

  @Test
  public void testContents() {
    //    assertEquals(null, Fn.contents());
  }

  @Test
  public void testLoop() {
    //    assertEquals(null, Fn.loop());
  }

  @Test
  public void testLoop1() {
    //    assertEquals(null, Fn.loop1());
  }

  @Test
  public void testDrop() {
    //    assertEquals(null, Fn.drop());
  }

  @Test
  public void testLast() {
    //    assertEquals(null, Fn.last());
  }

  @Test
  public void testGlue() {
    //    assertEquals(null, Fn.glue());
  }

  @Test
  public void testSplit1() {
    //    assertEquals(null, Fn.split1());
  }

  @Test
  public void testLast1() {
    //    assertEquals(null, Fn.last1());
  }

  @Test
  public void testFirst() {
    //    assertEquals(null, Fn.first());
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
    //    assertEquals(null, Fn.reverse());
  }

  @Test
  public void testToEnumeration() {
    //    assertEquals(null, Fn.toEnumeration());
  }

  @Test
  public void testUnique() {
    //    assertEquals(null, Fn.unique());
  }

  @Test
  public void testConcat() {
    //    assertEquals(null, Fn.concat());
  }

  @Test
  public void testConcat1() {
    //    assertEquals(null, Fn.concat1());
  }

  @Test
  public void testEach() {
    //    assertEquals(null, Fn.each());
  }

  @Test
  public void testWithNext() {
    //    assertEquals(null, Fn.withNext());
  }

  @Test
  public void testTake() {
    //    assertEquals(null, Fn.take());
  }

  @Test
  public void testToSet() {
    //    assertEquals(null, Fn.toSet());
  }

  @Test
  public void testIndex() {
    //    assertEquals(null, Fn.index());
  }

  @Test
  public void testBreadth() {
    //    assertEquals(null, Fn.breadth());
  }

  @Test
  public void testMultiply() {
    //    assertEquals(null, Fn.multiply());
  }

  @Test
  public void testBreadthPaths() {
    //    assertEquals(null, Fn.breadthPaths());
  }

  @Test
  public void testDepth() {
    //    assertEquals(null, Fn.depth());
  }

  @Test
  public void testGroup() {
    //    assertEquals(null, Fn.group());
  }

  @Test
  public void testGroup1() {
    //    assertEquals(null, Fn.group1());
  }

  @Test
  public void testSize() {
    //    assertEquals(null, Fn.size());
  }

  @Test
  public void testToString() {
    //    assertEquals(null, Fn.toString());
  }

  @Test
  public void testConvert() {
    //    assertEquals(null, Fn.convert());
  }

  @Test
  public void testStrings() {
    //    assertEquals(null, Fn.strings());
  }

  @Test
  public void testJoin() {
    //    assertEquals(null, Fn.join());
  }

  @Test
  public void testPairs() {
    //    assertEquals(null, Fn.pairs());
  }

  @Test
  public void testPairs1() {
    //    assertEquals(null, Fn.pairs1());
  }

  @Test
  public void testSort() {
    //    assertEquals(null, Fn.sort());
  }

  @Test
  public void testSubtract() {
    //    assertEquals(null, Fn.subtract());
  }

  @Test
  public void testIntersection() {
    //    assertEquals(null, Fn.intersection());
  }

  @Test
  public void testDifference() {
    //    assertEquals(null, Fn.difference());
  }

  @Test
  public void testUnion() {
    //    assertEquals(null, Fn.union());
  }

  @Test
  public void testJoin1() {
    //    assertEquals(null, Fn.join1());
  }

  @Test
  public void testJoin2() {
    //    assertEquals(null, Fn.join2());
  }

  @Test
  public void testLeftJoin() {
    //    assertEquals(null, Fn.leftJoin());
  }

  @Test
  public void testJoin3() {
    //    assertEquals(null, Fn.join3());
  }

  @Test
  public void testLeftJoin1() {
    //    assertEquals(null, Fn.leftJoin1());
  }
}
