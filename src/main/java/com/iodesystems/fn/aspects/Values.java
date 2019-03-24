package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.Generator;

public class Values {

  public static <A> A ifNull(A thing, A ifNull) {
    if (thing == null) {
      return ifNull;
    }
    return thing;
  }

  public static <A> A ifNull(A thing, Generator<A> ifNull) {
    if (thing == null) {
      return ifNull.next();
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
