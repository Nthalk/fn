package com.iodesystems.fn.aspects;

import java.util.Set;

public class Sets {

  public static <A> Iterable<A> subtract(Iterable<A> these, Iterable<A> fromThese) {
    Set<A> toRemove = Iterables.toSet(these);
    return Iterables.where(fromThese, a -> !toRemove.contains(a));
  }

  public static <A> Iterable<A> intersection(Iterable<A> these, Iterable<A> andThese) {
    Set<A> toRemove = Iterables.toSet(these);
    return Iterables.where(andThese, toRemove::contains);
  }

  public static <A> Iterable<A> difference(Iterable<A> these, Iterable<A> andThese) {
    Set<A> removeFromAndThese = Iterables.toSet(these);
    Set<A> removeFromThese = Iterables.toSet(andThese);

    return Iterables.concat(
        Iterables.where(these, o -> !removeFromThese.contains(o)),
        Iterables.where(andThese, o1 -> !removeFromAndThese.contains(o1)));
  }

  public static <A> Iterable<A> union(Iterable<A> these, Iterable<A> andThese) {
    return Iterables.unique(Iterables.concat(these, andThese));
  }
}
