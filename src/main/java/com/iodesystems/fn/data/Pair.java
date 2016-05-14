package com.iodesystems.fn.data;

public class Pair<A, B> {
    private static final From<Pair<?, ?>, ?> EXTRACT_A = new From<Pair<?, ?>, Object>() {
        @Override
        public Object from(Pair<?, ?> aPair) {
            return aPair.getA();
        }
    };
    private static final From<Pair<?, ?>, ?> EXTRACT_B = new From<Pair<?, ?>, Object>() {
        @Override
        public Object from(Pair<?, ?> aPair) {
            return aPair.getB();
        }
    };
    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @SuppressWarnings("unchecked")
    public static <A> From<Pair<A, ?>, A> extractA() {
        return (From) EXTRACT_A;
    }

    @SuppressWarnings("unchecked")
    public static <B> From<Pair<?, B>, B> extractB() {
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

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (a != null ? !a.equals(pair.a) : pair.a != null) return false;
        return b != null ? b.equals(pair.b) : pair.b == null;

    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        return result;
    }
}