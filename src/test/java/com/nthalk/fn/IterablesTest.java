package com.nthalk.fn;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IterablesTest {

    @Test
    public void testDrop() {
        assertEquals(Arrays.asList(2, 3), Fn.of(1, 2, 3).drop(1).toList());
    }

    @Test
    public void testTake() {
        assertEquals(Arrays.asList(1, 2), Fn.of(1, 2, 3).take(2).toList());
    }

    @Test
    public void testMultiply() {
        Fn<Integer> multiply = Fn.of(1, 2, 3).multiply(new From<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> from(Integer integer) {
                return Fn.of(integer).repeat(integer);
            }
        });

        assertEquals(6, multiply.size());
        assertEquals(3, multiply.unique().size());

        Map<Integer, List<Integer>> group = multiply.group(new From<Integer, Integer>() {
            @Override
            public Integer from(Integer integer) {
                return integer;
            }
        });

        assertEquals(1, group.get(1).size());
        assertEquals(2, group.get(2).size());
        assertEquals(3, group.get(3).size());
    }

}