package com.iodesystems.fn.aspects;

public class Values {

  public static <A> A ifNull(A thing, A ifNull) {
    if (thing == null) {
      return ifNull;
    }
    return thing;
  }

  public static <A> boolean isEqual(A value, A value1) {
    if (value == null) {
      return value1 == null;
    }
    return value.equals(value1);
  }
}
