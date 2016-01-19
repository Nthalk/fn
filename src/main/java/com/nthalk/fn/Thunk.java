package com.nthalk.fn;

public abstract class Thunk<A, B, C extends Option<B>> implements From<A, C> {
    public static <A, B, C extends Option<B>> Thunk<A, B, C> of(final From<A, C> from) {
        return new Thunk<A, B, C>() {
            public C from(A a) {
                return from.from(a);
            }
        };
    }

    public Thunk<A, B, C> or(final From<A, C> from) {
        return or(Thunk.of(from));
    }

    public Thunk<A, B, C> or(final Thunk<A, B, C> thunk) {
        return new Thunk<A, B, C>() {
            public C from(A a) {
                C from = Thunk.this.from(a);
                if (from.isPresent()) {
                    return from;
                } else {
                    return thunk.from(a);
                }
            }
        };
    }

    public Thunk<A, B, C> before(final From<A, C> from) {
        return before(Thunk.of(from));
    }

    public Thunk<A, B, C> before(final Thunk<A, B, C> thunk) {
        return new Thunk<A, B, C>() {
            public C from(A a) {
                C from = thunk.from(a);
                if (from.isPresent()) {
                    return from;
                } else {
                    return Thunk.this.from(a);
                }
            }
        };
    }
}
