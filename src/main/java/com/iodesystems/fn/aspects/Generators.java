package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.Generator;

public class Generators {

  public static <T> Generator<T> of(T initial, From<T, T> next) {
    return new Generator<T>() {
      T current = initial;

      @Override
      public T next() {
        T tmp = current;
        current = next.from(current);
        return tmp;
      }
    };
  }
}
