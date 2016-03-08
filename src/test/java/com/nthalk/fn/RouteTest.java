package com.nthalk.fn;

import com.nthalk.fn.tuples.Tuple2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RouteTest {
    @Test
    public void testRoute() {
        Route<String, Integer> five = Fn.route(new From<String, Option<Integer>>() {
            @Override
            public Option<Integer> from(String s) {
                if ("five".equalsIgnoreCase(s)) {
                    return Option.of(5);
                } else {
                    return Option.empty();
                }
            }
        });

        Route<String, Integer> six = Fn.route(new From<String, Option<Integer>>() {
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

    /**
     * This is a horrific use of routes.
     * It's bad by even Enterprise FizzBuzz standards
     */
    @Test
    public void testHowToFailAnInterview() {
        final Route<Integer, String> fizz = Fn.route(new From<Integer, Option<String>>() {
            @Override
            public Option<String> from(Integer integer) {
                return integer % 3 == 0 ? Option.of("Fizz") : Option.<String>empty();
            }
        });

        final Route<Integer, String> buzz = Fn.route(new From<Integer, Option<String>>() {
            @Override
            public Option<String> from(Integer integer) {
                return integer % 5 == 0 ? Option.of("Buzz") : Option.<String>empty();
            }
        });

        Route<Integer, String> fizzBuzz = Fn.route(new From<Integer, Option<String>>() {
            @Override
            public Option<String> from(Integer integer) {
                if (fizz.from(integer).isPresent() && buzz.from(integer).isPresent()) {
                    return Option.of("FizzBuzz");
                } else {
                    return Option.empty();
                }

            }
        });

        From<Integer, String> theNumber = new From<Integer, String>() {
            @Override
            public String from(Integer integer) {
                return integer.toString();
            }
        };

        for (String line : Fn  // Start with anything...
            .of(0)
            // Get our count
            .repeat(100)
            // Abuse our index
            .withIndex()
            .drop(1)
            .from(new From<Tuple2<Integer, Integer>, Integer>() {
                @Override
                public Integer from(Tuple2<Integer, Integer> tuple2) {
                    return tuple2.getA();
                }
            })
            // Application logic!
            .from(fizzBuzz.or(fizz).or(buzz).orElse(theNumber))) {
            System.out.println(line);
        }

    }
}
