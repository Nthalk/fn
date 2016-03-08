package com.iodesystems.fn.tuples;

import com.iodesystems.fn.From;

public class Tuple2<A, B> {
    private static final From<Tuple2<?, ?>, ?> EXTRACT_A = new From<Tuple2<?, ?>, Object>() {
        @Override
        public Object from(Tuple2<?, ?> aTuple2) {
            return aTuple2.getA();
        }
    };
    private static final From<Tuple2<?, ?>, ?> EXTRACT_B = new From<Tuple2<?, ?>, Object>() {
        @Override
        public Object from(Tuple2<?, ?> aTuple2) {
            return aTuple2.getB();
        }
    };
    private final A a;
    private final B b;

    public Tuple2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @SuppressWarnings("unchecked")
    public static <A> From<Tuple2<A, ?>, A> extractA() {
        return (From) EXTRACT_A;
    }

    @SuppressWarnings("unchecked")
    public static <B> From<Tuple2<?, B>, B> extractB() {
        return (From) EXTRACT_B;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

        return a != null ? a.equals(tuple2.a) : tuple2.a == null && (b != null ? b.equals(tuple2.b) : tuple2.b == null);

    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        return result;
    }
}
