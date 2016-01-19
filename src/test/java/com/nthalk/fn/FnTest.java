package com.nthalk.fn;

import org.junit.Test;

import java.util.List;
import java.util.Map;

public class FnTest {
    @Test
    public void kitchenSink() {

        From<String, Option<Integer>> of = Fn.thunk(new From<String, Option<Integer>>() {
            public Option<Integer> from(String s) {
                if (s.length() > 10) {
                    return Option.empty();
                } else {
                    return Option.of(s.length());
                }
            }
        }).or(Fn.thunk(new From<String, Option<Integer>>() {
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

        System.out.println();
    }
}
