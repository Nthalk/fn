package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.From2;
import java.util.Iterator;

public class SizedIterables {

  public static <A, B> Iterable<B> of(
      A source, From<A, Integer> getSize, From2<A, Integer, B> getItem) {
    return new Iterable<B>() {
      @Override
      public Iterator<B> iterator() {
        return new Iterator<B>() {
          final int size = getSize.from(source);
          int index = 0;

          @Override
          public boolean hasNext() {
            return index < size;
          }

          @Override
          public B next() {
            return getItem.from(source, index++);
          }
        };
      }
    };
  }
}
