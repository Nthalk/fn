package com.iodesystems.fn;

public abstract class FromChain<A, B> implements From<A, B> {

    public static <A, B> FromChain<A, B> of(final From<A, B> from) {
        return new FromChain<A, B>() {
            @Override
            public B from(A a) {
                return from.from(a);
            }
        };
    }

    public <C> FromChain<A, C> then(final From<B, C> next) {
        return new FromChain<A, C>() {
            @Override
            public C from(A a) {
                return next.from(FromChain.this.from(a));
            }
        };
    }
}
