package com.iodesystems.fn.aspects;

import com.iodesystems.fn.data.From;
import com.iodesystems.fn.tree.NodeWithParent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Trees {

  public static <A> Iterable<A> depth(
      final Iterable<A> sources, final From<A, Iterable<A>> multiplier) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          final Iterator<A> source = sources.iterator();
          final List<Iterator<A>> descent = new LinkedList<>();
          A nextA;

          @Override
          public boolean hasNext() {
            while (!descent.isEmpty()) {
              int lastIndex = descent.size() - 1;
              Iterator<A> top = descent.get(lastIndex);
              if (!top.hasNext()) {
                descent.remove(lastIndex);
              } else {
                nextA = top.next();
                descent.add(multiplier.from(nextA).iterator());
                return true;
              }
            }

            if (source.hasNext()) {
              nextA = source.next();
              descent.add(multiplier.from(nextA).iterator());
              return true;
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

  public static <A> Iterable<A> breadth(
      final Iterable<A> sources, final From<A, Iterable<A>> descend) {
    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {
          final List<A> todo = new LinkedList<>();
          Iterator<A> currentLevel = sources.iterator();
          A nextA;

          @Override
          public boolean hasNext() {
            if (currentLevel.hasNext()) {
              nextA = currentLevel.next();
              todo.add(nextA);
              return true;
            } else if (!todo.isEmpty()) {
              currentLevel = descend.from(todo.remove(0)).iterator();
              return hasNext();
            } else {
              return false;
            }
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

  public static <A> Iterable<List<A>> breadthPaths(
      final Iterable<A> sources, final From<A, Iterable<A>> multiplier) {
    return new Iterable<List<A>>() {
      @Override
      public Iterator<List<A>> iterator() {
        return new Iterator<List<A>>() {
          private final LinkedList<NodeWithParent<A>> todo = new LinkedList<>();
          private NodeWithParent<A> parent;
          private NodeWithParent<A> current;
          private Iterator<A> currentLevel = sources.iterator();

          @Override
          public boolean hasNext() {
            if (currentLevel.hasNext()) {
              current = new NodeWithParent<>(parent, currentLevel.next());
              todo.add(current);
              return true;
            } else if (!todo.isEmpty()) {
              parent = todo.removeFirst();
              currentLevel = multiplier.from(parent.getItem()).iterator();
              return hasNext();
            } else {
              return false;
            }
          }

          @Override
          public List<A> next() {
            LinkedList<A> path = new LinkedList<>();
            path.add(current.getItem());
            while (current.getParent() != null) {
              current = current.getParent();
              path.addFirst(current.getItem());
            }
            return path;
          }

          @Override
          public void remove() {
            throw new IllegalStateException();
          }
        };
      }
    };
  }
}
