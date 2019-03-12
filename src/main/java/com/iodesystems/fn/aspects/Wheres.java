package com.iodesystems.fn.aspects;

import com.iodesystems.fn.logic.Where;
import java.util.Objects;

public class Wheres {

  public static final Where<Object> ISNULL = Objects::nonNull;

  public static final Where<Object> NOTNULL = Objects::nonNull;

  public static <T> Where<T> notNull() {
    return (Where<T>) NOTNULL;
  }

  public static <T> Where<T> isNull() {
    return (Where<T>) ISNULL;
  }

  public static <T> Where<T> is(Class<T> cls) {
    return t -> t != null && cls.isAssignableFrom(t.getClass());
  }

  public static <T> Where<T> is(T value) {
    return t -> Values.isEqual(t, value);
  }

  public static <T> Where<T> not(Class<T> cls) {
    return t -> t == null || !cls.isAssignableFrom(t.getClass());
  }

  public static <T> Where<T> not(T value) {
    return t -> !Values.isEqual(t, value);
  }

  public static <T> Where<T> not(Where<T> condition) {
    return t -> !condition.is(t);
  }
}
