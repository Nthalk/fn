package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.CloseableIterable;
import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.From2;
import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class SizedIterables {

  public static <A, B> CloseableIterable<B> of(
      A source, From<A, Integer> getSize, From2<A, Integer, B> getItem) {
    return new CloseableIterable<B>() {
      @Override
      public void close() throws IOException {
        if (source instanceof Closeable) {
          ((Closeable) source).close();
        }
      }

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
