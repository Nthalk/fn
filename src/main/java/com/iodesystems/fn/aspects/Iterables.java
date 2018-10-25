package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.Combine;
import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.Generator;
import com.iodesystems.fn.data.Option;
import com.iodesystems.fn.data.Pair;
import com.iodesystems.fn.logic.Handler;
import com.iodesystems.fn.logic.Where;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Iterables {

  public static final Iterator<Object> EMPTY_ITERATOR =
      new Iterator<Object>() {

        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public Object next() {
          return null;
        }
      };

  public static final Iterable<Object> EMPTY =
      new Iterable<Object>() {
        @Override
        public Iterator<Object> iterator() {
          return EMPTY_ITERATOR;
        }
      };

  public static <A> Option<A> first(Iterable<A> as) {
    Iterator<A> iterator = as.iterator();
    if (iterator.hasNext()) {
      return Option.of(iterator.next());
    } else {
      return Option.empty();
    }
  }

  public static <A> Option<A> last(Iterable<A> as) {
    A last = null;
    for (A a : as) {
      last = a;
    }
    return Option.of(last);
  }

  public static <A> Iterable<A> take(final int count, final Iterable<A> source) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          final Iterator<A> parent = source.iterator();
          int soFar = 0;

          @Override
          public boolean hasNext() {
            return soFar++ < count && parent.hasNext();
          }

          @Override
          public A next() {
            return parent.next();
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> Iterable<A> drop(final int count, final Iterable<A> source) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          final Iterator<A> parent = source.iterator();
          int skip = 0;

          @Override
          public boolean hasNext() {
            while (skip++ < count && parent.hasNext()) {
              parent.next();
            }
            return parent.hasNext();
          }

          @Override
          public A next() {
            return parent.next();
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> Iterable<A> concat(final Iterable<A> a, final Iterable<A> b) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          final Iterator<A> nextB = b.iterator();
          Iterator<A> current = a.iterator();
          final Iterator<A> first = current;

          public boolean hasNext() {
            if (current.hasNext()) {
              return true;
            } else if (current == first) {
              current = nextB;
              return current.hasNext();
            }
            return false;
          }

          public A next() {
            return current.next();
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A, B> Iterable<Iterable<B>> multiply(
      final Iterable<A> sources, final From<A, Iterable<B>> multiplier) {
    return new Iterable<Iterable<B>>() {
      @Override
      public Iterator<Iterable<B>> iterator() {
        return new Iterator<Iterable<B>>() {
          final Iterator<A> sourceA = sources.iterator();
          Iterable<B> next = null;

          @Override
          public boolean hasNext() {
            if (sourceA.hasNext()) {
              next = multiplier.from(sourceA.next());
              return true;
            }
            return false;
          }

          @Override
          public Iterable<B> next() {
            return next;
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A, B> Iterable<B> convert(final Iterable<A> source, final From<A, B> from) {

    return new Iterable<B>() {
      @Override
      public Iterator<B> iterator() {
        final Iterator<A> sourceItems = source.iterator();
        return new Iterator<B>() {
          public boolean hasNext() {
            return sourceItems.hasNext();
          }

          public B next() {
            return from.from(sourceItems.next());
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> Iterable<A> unique(Iterable<A> as) {
    final Set<A> uniques = new HashSet<>();
    return where(as, uniques::add);
  }

  public static <A, B> Iterable<B> where(final Iterable<A> source, final Class<B> cls) {
    //noinspection unchecked
    return (Iterable<B>) where(source, (Where<A>) Wheres.is(cls));
  }

  public static <A> Iterable<A> where(final Iterable<A> source, final Where<A> where) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          final Iterator<A> parent = source.iterator();
          A current;

          @Override
          public boolean hasNext() {
            while (parent.hasNext()) {
              current = parent.next();
              if (where.is(current)) {
                return true;
              }
            }
            return false;
          }

          @Override
          public A next() {
            return current;
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> void consume(Iterable<A> as) {
    //noinspection StatementWithEmptyBody
    for (A ignored : as) {}
  }

  public static <A> int size(Iterable<A> as) {
    if (as instanceof Collection) {
      return ((Collection<A>) as).size();
    } else {
      int i = 0;
      for (A ignored : as) {
        i += 1;
      }
      return i;
    }
  }

  public static <A> Enumeration<A> toEnumeration(Iterable<A> contents) {
    final Iterator<A> iterator = contents.iterator();
    return new Enumeration<A>() {

      @Override
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }

      @Override
      public A nextElement() {
        return iterator.next();
      }
    };
  }

  public static <A> List<A> toList(Iterable<A> contents) {
    if (contents instanceof List) {
      return (List<A>) contents;
    }
    List<A> list = new ArrayList<>();
    for (A content : contents) {
      list.add(content);
    }
    return list;
  }

  public static <A> Iterable<A> of(final Generator<A> generator) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          A nextA;

          @Override
          public boolean hasNext() {
            nextA = generator.next();
            return nextA != null;
          }

          @Override
          public A next() {
            return nextA;
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> Iterable<A> of(final Enumeration<A> source) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          @Override
          public boolean hasNext() {
            return source.hasMoreElements();
          }

          @Override
          public A next() {
            return source.nextElement();
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> Iterable<A> of(A source) {
    return Collections.singletonList(source);
  }

  @SafeVarargs
  public static <A> Iterable<A> of(A... source) {
    return Arrays.asList(source);
  }

  public static <A, B> Iterable<Pair<A, B>> parallel(final Iterable<A> as, final Iterable<B> bs) {
    return new Iterable<Pair<A, B>>() {
      @Override
      public Iterator<Pair<A, B>> iterator() {
        return new Iterator<Pair<A, B>>() {
          final Iterator<A> sourceA = as.iterator();
          final Iterator<B> sourceB = bs.iterator();

          @Override
          public boolean hasNext() {
            return sourceA.hasNext() && sourceB.hasNext();
          }

          @Override
          public Pair<A, B> next() {
            return new Pair<>(sourceA.next(), sourceB.next());
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> Iterable<A> repeat(final A a, final int times) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          int count = 0;

          @Override
          public boolean hasNext() {
            return times == -1 || ++count <= times;
          }

          @Override
          public A next() {
            return a;
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> Iterable<A> loop(final Iterable<A> as, final int times) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          int count = 0;
          Iterator<A> source = as.iterator();

          @Override
          public boolean hasNext() {
            if (source.hasNext()) {
              return true;
            } else if (times == -1 || ++count < times) {
              source = as.iterator();
              return source.hasNext();
            }
            return false;
          }

          @Override
          public A next() {
            return source.next();
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  @SuppressWarnings("unchecked")
  public static <A> Iterable<A> empty() {
    return (Iterable<A>) EMPTY;
  }

  public static <A, B> Option<B> first(Iterable<A> as, Class<B> cls) {
    return first(where(as, cls));
  }

  public static <A> Option<A> first(Iterable<A> as, Where<A> where) {
    for (A a : as) {
      if (where.is(a)) {
        return Option.of(a);
      }
    }
    return Option.empty();
  }

  public static <A> Option<A> last(Iterable<A> as, Where<A> where) {
    A last = null;
    for (A a : as) {
      if (where.is(a)) {
        last = a;
      }
    }
    return Option.of(last);
  }

  public static <A> Iterable<Iterable<A>> split(
      final Iterable<A> contents, final Where<A> splitter) {
    return new Iterable<Iterable<A>>() {
      @Override
      public Iterator<Iterable<A>> iterator() {
        return new Iterator<Iterable<A>>() {
          final Iterator<A> source = contents.iterator();
          List<A> segment;
          boolean isTrailingEnd;
          A trailingItem;

          @Override
          public boolean hasNext() {
            segment = new ArrayList<>();
            if (trailingItem != null) {

              segment.add(trailingItem);
              trailingItem = null;
            }

            while (source.hasNext()) {
              A next = source.next();
              if (splitter.is(next)) {
                isTrailingEnd = true;
                trailingItem = next;
                return true;
              } else {
                segment.add(next);
              }
            }

            if (isTrailingEnd) {
              isTrailingEnd = false;
              return true;
            } else {
              return !segment.isEmpty();
            }
          }

          @Override
          public Iterable<A> next() {
            return segment;
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A, B extends Iterable<A>> Iterable<A> flatten(Iterable<B> contents) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          final Iterator<B> iterator = contents.iterator();
          Iterator<A> next;

          @Override
          public boolean hasNext() {
            if (next != null && next.hasNext()) {
              return true;
            } else {
              next = null;
            }
            while (iterator.hasNext()) {
              next = this.iterator.next().iterator();
              if (next.hasNext()) {
                return true;
              }
            }
            return false;
          }

          @Override
          public A next() {
            return next.next();
          }
        };
      }
    };
  }

  public static <B, A> B combine(Iterable<A> contents, B initial, Combine<A, B> condenser) {
    B condensate = initial;
    for (A content : contents) {
      condensate = condenser.from(content, condensate);
    }
    return condensate;
  }

  public static <A> Iterable<A> takeWhile(final Iterable<A> contents, final Where<A> where) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          final Iterator<A> source = contents.iterator();
          A nextA = null;

          @Override
          public boolean hasNext() {
            if (source.hasNext()) {
              nextA = source.next();
              return where.is(nextA);
            }
            return false;
          }

          @Override
          public A next() {
            return nextA;
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> Iterable<A> dropWhile(final Iterable<A> contents, final Where<A> where) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        final Iterator<A> source = contents.iterator();

        return new Iterator<A>() {
          A first = null;
          boolean yieldedFirst = false;

          @Override
          public boolean hasNext() {
            if (first == null) {
              while (source.hasNext()) {
                first = source.next();
                if (!where.is(first)) {
                  return true;
                }
              }
            }
            return source.hasNext();
          }

          @Override
          public A next() {
            if (!yieldedFirst) {
              yieldedFirst = true;
              return first;
            }
            return source.next();
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> Set<A> toSet(Iterable<A> contents) {
    if (contents instanceof Set) {
      return (Set<A>) contents;
    }
    Set<A> set = new HashSet<>();
    for (A content : contents) {
      set.add(content);
    }
    return set;
  }

  public static <A> Iterable<A> loop(final Iterable<A> contents) {
    return new Iterable<A>() {
      Iterator<A> iterator = contents.iterator();
      boolean first = true;

      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          @Override
          public boolean hasNext() {
            if (first) {
              first = false;
              return iterator.hasNext();
            } else if (!iterator.hasNext()) {
              iterator = contents.iterator();
            }
            return true;
          }

          @Override
          public A next() {
            return iterator.next();
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }

  public static <A> A[] toArray(Iterable<A> contents, A[] preallocated) {
    int i = 0;
    for (A content : contents) {
      preallocated[i++] = content;
    }
    return preallocated;
  }

  public static <A> Iterable<A> withNext(
      final Iterable<A> contents, final From<A, A> nextFromCurrent) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          A current = null;
          final Iterator<A> original = contents.iterator();

          @Override
          public boolean hasNext() {
            if (current != null) {
              current = nextFromCurrent.from(current);
              if (current != null) {
                return true;
              }
            }
            return original.hasNext();
          }

          @Override
          public A next() {
            if (current != null) {
              return current;
            } else {
              return current = original.next();
            }
          }
        };
      }
    };
  }

  public static <A> Iterable<A> each(final Iterable<A> contents, final Handler<A> handler) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          final Iterator<A> parent = contents.iterator();

          @Override
          public boolean hasNext() {
            return parent.hasNext();
          }

          @Override
          public A next() {
            A next = parent.next();
            handler.handle(next);
            return next;
          }
        };
      }
    };
  }
}
