package com.nthalk.fn;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class FilterTest {
    @Test
    public void testFilter() {
        List<Integer> source = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> notTwo = Fn.filter(source, Fn.not(2));
        List<Integer> isTwo = Fn.filter(source, Fn.is(2));
        List<Integer> isEven = Fn.filter(source, new Where<Integer>() {
            @Override
            public boolean is(Integer integer) {
                return integer % 2 == 0;
            }
        });

        for (Integer integer : notTwo) {
            assertTrue(integer != 2);
        }

        for (Integer integer : isTwo) {
            assertTrue(integer == 2);
        }

        for (Integer integer : isEven) {
            assertTrue(integer % 2 == 0);
        }
    }


}
