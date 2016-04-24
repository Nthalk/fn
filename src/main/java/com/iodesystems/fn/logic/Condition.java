package com.iodesystems.fn.logic;

public abstract class Condition<A> implements Where<A> {
    public static <A> Condition<A> of(final Where<A> where) {
        return new Condition<A>() {
            @Override
            public boolean is(A a) {
                return where.is(a);
            }
        };
    }

    public Condition<A> or(final Where<A> where) {
        return new Condition<A>() {
            @Override
            public boolean is(A a) {
                return Condition.this.is(a) || where.is(a);
            }
        };
    }

    public Condition<A> and(final Where<A> where) {
        return new Condition<A>() {
            @Override
            public boolean is(A a) {
                return Condition.this.is(a) && where.is(a);
            }
        };
    }


}
