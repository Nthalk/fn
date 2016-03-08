package com.nthalk.fn;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RouteTest {
    @Test
    public void testRoute() {
        Route<String, Integer> five = Fn.partial(new From<String, Option<Integer>>() {
            @Override
            public Option<Integer> from(String s) {
                if ("five".equalsIgnoreCase(s)) {
                    return Option.of(5);
                } else {
                    return Option.empty();
                }
            }
        });

        Route<String, Integer> six = Fn.partial(new From<String, Option<Integer>>() {
            @Override
            public Option<Integer> from(String s) {
                if ("six".equalsIgnoreCase(s)) {
                    return Option.of(6);
                } else {
                    return Option.empty();
                }
            }
        });

        Route<String, Integer> fiveOrSix = five.or(six);
        assertEquals(fiveOrSix.from("five"), Option.of(5));
        assertEquals(fiveOrSix.from("six"), Option.of(6));
        assertEquals(fiveOrSix.from("seven"), Option.<Integer>empty());

    }
}
