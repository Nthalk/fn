package com.iodesystems.fn.data;

public interface From<A, B> {
    From<?, ?> IDENTITY = new From<Object, Object>() {
        @Override
        public Object from(Object o) {
            return o;
        }
    };

    B from(A a);
}
