package com.iodesystems.fn.data;

public interface From<A, B> {
    From<?, ?> IDENTITY = (From<Object, Object>) o -> o;

    B from(A a);
}
