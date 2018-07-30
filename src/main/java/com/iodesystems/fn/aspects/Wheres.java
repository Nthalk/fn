package com.iodesystems.fn.aspects;

import com.iodesystems.fn.logic.Where;
import java.util.Objects;

public class Wheres {

  public static <T> Where<T> notNull() {
    return Objects::nonNull;
  }

  public static <T> Where<T> isNull() {
    return Objects::isNull;
  }

  public static <T> Where<T> is(Class<T> cls) {
    return new Where<T>() {
      @Override
      public boolean is(T t) {
        return t != null && cls.isAssignableFrom(t.getClass());
      }
    };
  }

  public static <T> Where<T> not(Class<T> cls) {
    return new Where<T>() {
      @Override
      public boolean is(T t) {
        return t == null || !cls.isAssignableFrom(t.getClass());
      }
    };
  }

  public static <T> Where<T> not(T value) {
    return new Where<T>() {
      @Override
      public boolean is(T t) {
        return Values.isEqual(t, value);
      }
    };
  }

  public static <T> Where<T> not(Where<T> condition) {
    return new Where<T>() {
      @Override
      public boolean is(T t) {
        return !condition.is(t);
      }
    };
  }
}
