package com.nthalk.fn;

public abstract class Partial<A, B> implements From<A, Option<B>> {
    public static <A, B> Partial<A, B> of(final From<A, Option<B>> from) {
        return new Partial<A, B>() {
            public Option<B> from(A a) {
                return from.from(a);
            }
        };
    }

    public Partial<A, B> or(final From<A, Option<B>> partial) {
        return new Partial<A, B>() {
            public Option<B> from(A a) {
                Option<B> from = Partial.this.from(a);
                if (from.isPresent()) {
                    return from;
                } else {
                    return partial.from(a);
                }
            }
        };
    }

    public Partial<A, B> before(final From<A, Option<B>> partial) {
        return new Partial<A, B>() {
            public Option<B> from(A a) {
                Option<B> from = partial.from(a);
                if (from.isPresent()) {
                    return from;
                } else {
                    return Partial.this.from(a);
                }
            }
        };
    }
}
