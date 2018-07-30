package com.iodesystems.fn.aspects;

import com.iodesystems.fn.logic.Where;
import java.util.Set;

public class Sets {

  public static <A> Iterable<A> subtract(Iterable<A> these, Iterable<A> fromThese) {
    Set<A> toRemove = Iterables.toSet(these);
    return Iterables.where(fromThese, new Where<A>() {
      @Override
      public boolean is(A a) {
        return !toRemove.contains(a);
      }
    });
  }

  public static <A> Iterable<A> intersection(Iterable<A> these, Iterable<A> andThese) {
    Set<A> toRemove = Iterables.toSet(these);
    return Iterables.where(andThese, toRemove::contains);
  }

  public static <A> Iterable<A> difference(Iterable<A> these, Iterable<A> andThese) {
    Set<A> removeFromAndThese = Iterables.toSet(these);
    Set<A> removeFromThese = Iterables.toSet(andThese);

    return Iterables.concat(
        Iterables.where(these, new Where<A>() {
          @Override
          public boolean is(A o) {
            return !removeFromThese.contains(o);
          }
        }),
        Iterables.where(andThese, new Where<A>() {
          @Override
          public boolean is(A o1) {
            return !removeFromAndThese.contains(o1);
          }
        }));
  }

  public static <A> Iterable<A> union(Iterable<A> these, Iterable<A> andThese) {
    return Iterables.unique(Iterables.concat(these, andThese));
  }
}
