package com.iodesystems.fn.data;

import java.util.Objects;

public class Pair<A, B> {

  private static final From<Pair<?, ?>, ?> EXTRACT_A = (From<Pair<?, ?>, Object>) Pair::getA;
  private static final From<Pair<?, ?>, ?> EXTRACT_B = (From<Pair<?, ?>, Object>) Pair::getB;
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

  public static <A, B> Pair<A, B> of(A a, B b) {
    return new Pair<>(a, b);
  }

  public A getA() {
    return a;
  }

  public B getB() {
    return b;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Pair<?, ?> pair = (Pair<?, ?>) o;

    if (!Objects.equals(a, pair.a)) {
      return false;
    }
    return Objects.equals(b, pair.b);
  }

  @Override
  public String toString() {
    return "Pair{" + "a=" + a + ", b=" + b + '}';
  }

  @Override
  public int hashCode() {
    int result = a != null ? a.hashCode() : 0;
    result = 31 * result + (b != null ? b.hashCode() : 0);
    return result;
  }
}
