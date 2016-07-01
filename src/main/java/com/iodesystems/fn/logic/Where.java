package com.iodesystems.fn.logic;

public interface Where<A> {
    Where<?> NOT_NULL = new Where<Object>() {
        @Override
        public boolean is(Object o) {
            return o == null;
        }
    };

    boolean is(A a);
}
