package com.nthalk.fn;

public abstract class Route<A, B> implements From<A, Option<B>> {
    public static <A, B> Route<A, B> of(final From<A, Option<B>> from) {
        return new Route<A, B>() {
            public Option<B> from(A a) {
                return from.from(a);
            }
        };
    }

    public Route<A, B> or(final From<A, Option<B>> partial) {
        return new Route<A, B>() {
            public Option<B> from(A a) {
                Option<B> from = Route.this.from(a);
                if (from.isPresent()) {
                    return from;
                } else {
                    return partial.from(a);
                }
            }
        };
    }

    public Route<A, B> before(final From<A, Option<B>> partial) {
        return new Route<A, B>() {
            public Option<B> from(A a) {
                Option<B> from = partial.from(a);
                if (from.isPresent()) {
                    return from;
                } else {
                    return Route.this.from(a);
                }
            }
        };
    }
}
