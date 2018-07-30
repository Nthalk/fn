package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.Pair;
import java.util.Iterator;

public class Pairs {

  public static <A, B> Iterable<Pair<A, B>> pairs(Iterable<B> contents, From<B, A> aExtractor) {
    return Iterables.convert(contents, new From<B, Pair<A, B>>() {
      @Override
      public Pair<A, B> from(B s) {
        return Pair.of(aExtractor.from(s), s);
      }
    });
  }

  public static <A, B, C> Iterable<Pair<A, B>> pairs(
      Iterable<C> contents, From<C, A> aExtractor, From<C, B> bExtractor) {
    return Iterables.convert(contents, new From<C, Pair<A, B>>() {
      @Override
      public Pair<A, B> from(C s) {
        return Pair.of(aExtractor.from(s), bExtractor.from(s));
      }
    });
  }

  public static <A> Iterable<Pair<Integer, A>> index(Iterable<A> contents) {
    return new Iterable<Pair<Integer, A>>() {
      @Override
      public Iterator<Pair<Integer, A>> iterator() {
        final Iterator<A> iterator = contents.iterator();
        return new Iterator<Pair<Integer, A>>() {
          int index = 0;

          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public Pair<Integer, A> next() {
            return Pair.of(index++, iterator.next());
          }
        };
      }
    };
  }
}
