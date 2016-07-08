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

    public Condition<A> or(final Where<A>... where) {
        return new Condition<A>() {
            @Override
            public boolean is(A a) {
                if (Condition.this.is(a)) {
                    return true;
                }
                for (Where<A> aWhere : where) {
                    if (aWhere.is(a)) return true;
                }
                return false;
            }
        };
    }

    public Condition<A> and(final Where<A>... where) {
        return new Condition<A>() {
            @Override
            public boolean is(A a) {
                if (!Condition.this.is(a)) {
                    return false;
                }
                for (Where<A> aWhere : where) {
                    if (!aWhere.is(a)) return false;
                }
                return true;
            }
        };
    }

    public static <V> Condition<V> not(final Where<V> filter) {
        return new Condition<V>() {
            @Override
            public boolean is(V v) {
                return !filter.is(v);
            }
        };
    }
}
