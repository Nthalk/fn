package com.iodesystems.fn.data;

public interface Combine<A, B> {
    B from(A a, B b);
}
