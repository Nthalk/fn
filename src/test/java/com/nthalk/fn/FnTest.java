package com.nthalk.fn;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FnTest {
    @Test
    public void kitchenSink() {

        From<String, Option<Integer>> of = Fn.partial(new From<String, Option<Integer>>() {
            public Option<Integer> from(String s) {
                if (s.length() > 10) {
                    return Option.empty();
                } else {
                    return Option.of(s.length());
                }
            }
        }).or(Fn.partial(new From<String, Option<Integer>>() {
            public Option<Integer> from(String s) {
                if (s.contains("bad")) {
                    return Option.empty();
                }
                return Option.of(10);
            }
        })).before(new From<String, Option<Integer>>() {
            public Option<Integer> from(String s) {
                if ("five".equals(s)) {
                    return Option.of(5);
                }
                return Option.empty();
            }
        });

        Iterable<Integer> converted = Fn.unwrap(
            Fn.from(
                Fn.filter(
                    Fn.join(Fn.of("a", "ab", "abc", "abcd", "five", "abcde", "abcdefhghghghg", "abcdefhghghghgbad"),
                            Fn.of("asdfasdfasdf")),
                    Fn.not("ab")),
                of));


        Map<String, List<Integer>> group = Fn.group(converted, new From<Integer, String>() {
            public String from(Integer integer) {
                return integer.toString();
            }
        });

        Map<Integer, Map.Entry<String, List<Integer>>> index = Fn.index(group.entrySet(), new From<Map.Entry<String, List<Integer>>, Integer>() {
            public Integer from(Map.Entry<String, List<Integer>> stringListEntry) {
                return stringListEntry.getValue().size();
            }
        });
    }

    @Test
    public void testRepeat() {
        assertEquals(9, Fn.of(1, 2, 3).repeat(3).size());
    }

    @Test
    public void testFrom() {
        int size = Fn
            .of(1, 2, 3, 4)
            .from(new From<Integer, Integer>() {
                @Override
                public Integer from(Integer integer) {
                    return integer;
                }
            }).size();

        assertEquals(4, size);
    }

    @Test
    public void testOptionIntegration() {
        int size = Fn
            .of(1, 2, 3, 4)
            // Expensive filter
            .from(new From<Integer, Option<Integer>>() {
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
