package com.iodesystems.fn;

public abstract class WhereCondition<A> implements Where<A> {
    public static <A> WhereCondition<A> of(final Where<A> where) {
        return new WhereCondition<A>() {
            @Override
            public boolean is(A a) {
                return where.is(a);
            }
        };
    }

    public WhereCondition<A> or(final Where<A> where) {
        return new WhereCondition<A>() {
            @Override
            public boolean is(A a) {
                return WhereCondition.this.is(a) || where.is(a);
            }
        };
    }

    public WhereCondition<A> and(final Where<A> where) {
        return new WhereCondition<A>() {
            @Override
            public boolean is(A a) {
                return WhereCondition.this.is(a) && where.is(a);
            }
        };
    }


}
