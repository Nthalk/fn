package com.nthalk.fn;

public interface Merge<A, B, C> {
    C from(A a, B b);

    interface Combine<A> extends Merge<A, A, A> {

    }
}
