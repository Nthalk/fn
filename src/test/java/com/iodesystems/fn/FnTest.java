package com.iodesystems.fn;

import com.iodesystems.fn.tuples.Tuple2;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FnTest {

    @Test
    public void testRepeat() {
        assertEquals(9, Fn.of(1, 2, 3).repeat(3).size());
    }

    @Test
    public void testFrom() {
        int size = Fn
            .of(1, 2, 3, 4)
            .convert(new From<Integer, Integer>() {
                @Override
                public Integer from(Integer integer) {
                    return integer;
                }
            }).size();

        assertEquals(4, size);
    }

    @Test
    public void testJoin() {
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6),
                     Fn.of(1, 2, 3).join(Fn.of(4, 5, 6)).toList());
    }

    @Test
    public void testAndOr() {
        assertEquals(Arrays.asList(2, 4), Fn.of(1, 2, 3, 4).filter(Fn.where(Fn.is(2)).or(Fn.is(4))).toList());
        assertEquals(Collections.singletonList(4), Fn.of(1, 2, 3, 4).filter(Fn.where(Fn.is(1)).or(Fn.is(4)).and(new Where<Integer>() {
            @Override
            public boolean is(Integer integer) {
                return integer % 2 == 0;
            }
        })).toList());
    }

    @Test
    public void testWithIndex() {
        Map<String, List<Tuple2<Integer, Integer>>> group = Fn
            .of(1, 2, 3, 4)
            .multiply(new From<Integer, Iterable<Integer>>() {
                @Override
                public Iterable<Integer> from(Integer integer) {
                    return Fn.repeat(integer, integer);
                }
            })
            .withIndex()
            .group(new From<Tuple2<Integer, Integer>, String>() {
                @Override
                public String from(Tuple2<Integer, Integer> integerIntegerTuple2) {
                    return integerIntegerTuple2.getB().toString();
                }
            });
        assertEquals(4, group.size());
        for (List<Tuple2<Integer, Integer>> tuple2s : group.values()) {
            assertEquals(tuple2s.get(0).getB().intValue(), tuple2s.size());
        }
    }

    @Test
    public void testUnwrap() {
        assertEquals(Arrays.asList(2, 4), Fn.of(Fn.unwrap(
            Fn.of(1, 2, 3, 4)
                .convert(new From<Integer, Option<Integer>>() {
                    @Override
                    public Option<Integer> from(Integer integer) {
                        return integer % 2 == 0 ? Option.of(integer) : Option.<Integer>empty();
                    }
                }))).toList());
    }

    @Test
    public void testOptionIntegration() {
        int size = Fn
            .of(1, 2, 3, 4)
            // Expensive filter
            .convert(new From<Integer, Option<Integer>>() {
                @Override
                public Option<Integer> from(Integer integer) {
                    return integer % 2 == 0 ? Option.of(integer) : Option.<Integer>empty();
                }
            })
            .multiply(new From<Option<Integer>, Iterable<Integer>>() {
                @Override
                public Iterable<Integer> from(Option<Integer> integers) {
                    return integers;
                }
            })
            .size();
        assertEquals(2, size);
    }

    @Test
    public void testIndex() {
        Map<String, Integer> index = Fn.of(1, 2, 3, 4).index(new From<Integer, String>() {
            @Override
            public String from(Integer integer) {
                return integer.toString();
            }
        });
        assertEquals(new Integer(1), index.get("1"));
    }

    @Test
    public void testSplit() {
        Fn<Iterable<Integer>> split = Fn.of(1, 2, 3, 4, 5, 6)
            .split(new Where<Integer>() {
                @Override
                public boolean is(Integer integer) {
                    return integer % 2 == 0;
                }
            });

        Fn<Integer> joined = Fn.of(Fn.flatten(split));
        assertEquals(3, joined.size());
        assertEquals(Option.empty(), joined.first(Fn.is(2)));

        Fn<Integer> joinedWithGlue = Fn.of(Fn.join(split, 2));
        assertEquals(6, joinedWithGlue.size());
        assertEquals(3, joinedWithGlue.filter(Fn.is(2)).size());
    }

    @Test
    public void testCombine() {
        Integer combine = Fn.of(1, 2, 3).combine(0, new Combine<Integer, Integer>() {
            @Override
            public Integer from(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        });
        assertEquals(new Integer(6), combine);
    }

    @Test
    public void testFn() throws Exception {
        Map<Integer, List<Integer>> group = Fn
            .of(1, 2, 3)
            .join(4, 8)
            // Strip out odds
            .filter(new Where<Integer>() {
                @Override
                public boolean is(Integer integer) {
                    return integer % 2 == 0;
                }
            })
            // Duplicate list
            .multiply(new From<Integer, Iterable<Integer>>() {
                @Override
                public Iterable<Integer> from(Integer integer) {
                    return Fn.of(integer, integer);
                }
            })
            // Unique list
            .unique()
            // Group by divisible by four
            .group(new From<Integer, Integer>() {
                @Override
                public Integer from(Integer integer) {
                    return integer % 4;
                }
            });


        assertEquals(2, group.size());
        assertEquals(Arrays.asList(4, 8), group.get(0));
        assertEquals(Collections.singletonList(2), group.get(2));
    }
}
