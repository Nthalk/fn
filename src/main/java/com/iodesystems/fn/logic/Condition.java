package com.iodesystems.fn.logic;

import java.util.Objects;

public abstract class Condition<A> implements Where<A> {

  public static <A> Condition<A> of(final Where<A> condition) {
    if (condition instanceof Condition) {
      return (Condition<A>) condition;
    } else {
      return new Condition<A>() {
        @Override
        public boolean is(A a) {
          return condition.is(a);
        }
      };
    }
  }

  public static <A> Condition<A> not(final Where<A> condition) {
    if (condition instanceof Condition) {
      return ((Condition<A>) condition).negate();
    } else {
      return new Condition<A>() {
        @Override
        public boolean is(A v) {
          return !condition.is(v);
        }
      };
    }
  }

  public static <A> Condition<A> isValue(A value) {
    if (value == null) {
      return Condition.of(Objects::isNull);
    } else {
      return Condition.of(value::equals);
    }
  }

  public static <A> Condition<A> isNotValue(A value) {
    if (value == null) {
      return Condition.of(obj -> !Objects.isNull(obj));
    } else {
      return Condition.of(obj -> !value.equals(obj));
    }
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

  public Condition<A> negate() {
    return new Condition<A>() {
      @Override
      public boolean is(A a) {
        return !Condition.this.is(a);
      }
    };
  }
}
