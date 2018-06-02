package com.iodesystems.fn;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class NextTest {
    @Test
    public void testLengthGetterIterator() {
        List<Integer> source = Fn.ofRange(1, 10).toList();
        SizedIndexAccessor sizedIndexAccessor = new SizedIndexAccessor(source);
        assertEquals(source, Fn.of(sizedIndexAccessor).iterateSizedIndexAccessors(SizedIndexAccessor::getSize, SizedIndexAccessor::get).toList());

    }

    @Test
    public void testNextSibilingIterator() {
        HasSibiling last = null;
        for (Integer value : Fn.range(1, 10)) {
            last = new HasSibiling(value, last);
        }
        assertEquals(10, Fn.of(last).iterateSibiling(HasSibiling::getSibiling).size());
    }

    class HasSibiling {
        private final int value;
        private final HasSibiling sibiling;

        public HasSibiling(int value, HasSibiling sibiling) {
            this.value = value;
            this.sibiling = sibiling;
        }

        @Override
        public String toString() {
            return "HasSibiling{" +
                    "value=" + value +
                    '}';
        }

        public HasSibiling getSibiling() {
            return sibiling;
        }
    }

    private class SizedIndexAccessor {
        private final List<Integer> wrapped;

        public SizedIndexAccessor(List<Integer> wrapped) {
            this.wrapped = wrapped;
        }

        public int getSize() {
            return wrapped.size();
        }

        public Integer get(Integer index) {
            return wrapped.get(index);
        }
    }
}
