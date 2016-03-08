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

    public <C> Route<A, C> from(final From<B, Option<C>> from) {
        return new Route<A, C>() {
            @Override
            public Option<C> from(A a) {
                Option<B> b = Route.this.from(a);
                if (b.isPresent()) {
                    return from.from(b.get());
                }
                return Option.empty();
            }
        };
    }

    public From<A, B> orElse(final From<A, B> orElse) {
        return new From<A, B>() {
            @Override
            public B from(A a) {
                Option<B> from = Route.this.from(a);
                if (from.isPresent()) {
                    return from.get();
                } else {
                    return orElse.from(a);
                }
            }
        };
    }
}
