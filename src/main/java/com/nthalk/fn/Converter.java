package com.nthalk.fn;

public interface Converter<A, B> extends From<A, B> {
    A backFrom(B b);
}
