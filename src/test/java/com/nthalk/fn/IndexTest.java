package com.nthalk.fn;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IndexTest {
    @Test
    public void testIndex() {
        Iterable<Integer> source = Fn.of(1, 2, 3, 4, 5);
        Map<String, Integer> index = Indexer.index(source, new From<Integer, String>() {
            @Override
            public String from(Integer integer) {
                return integer.toString();
            }
        });
        assertEquals(index.size(), 5);
        for (Integer integer : source) {
            assertTrue(index.containsKey(integer.toString()));
        }
    }
}
