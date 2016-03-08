package com.iodesystems.fn;

public interface Combine<A, B> {
    B from(A a, B b);
}
