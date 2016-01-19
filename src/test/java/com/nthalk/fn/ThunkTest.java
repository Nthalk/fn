package com.nthalk.fn;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ThunkTest {
    @Test
    public void testThunk() {
        Thunk<String, Integer, Option<Integer>> five = Fn.thunk(new From<String, Option<Integer>>() {
            @Override
            public Option<Integer> from(String s) {
                if ("five".equalsIgnoreCase(s)) {
                    return Option.of(5);
                } else {
                    return Option.empty();
                }
            }
        });

        Thunk<String, Integer, Option<Integer>> six = Fn.thunk(new From<String, Option<Integer>>() {
            @Override
            public Option<Integer> from(String s) {
                if ("six".equalsIgnoreCase(s)) {
                    return Option.of(6);
                } else {
                    return Option.empty();
                }
            }
        });

        Thunk<String, Integer, Option<Integer>> fiveOrSix = five.or(six);
        assertEquals(fiveOrSix.from("five"), Option.of(5));
        assertEquals(fiveOrSix.from("six"), Option.of(6));
        assertEquals(fiveOrSix.from("seven"), Option.<Integer>empty());

    }
}
