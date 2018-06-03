package com.iodesystems.fn.data;


import com.iodesystems.fn.logic.Where;
import com.iodesystems.fn.tree.NodeWithParent;

import java.util.*;

public abstract class Iterables {

    public static final Iterator<?> EMPTY_ITERATOR = new Iterator<Object>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }
    };

    public static final Iterable<?> EMPTY = (Iterable<Object>) Iterables::iterator;

    public static <A> Iterable<A> take(final int count, final Iterable<A> source) {
        return () -> new Iterator<A>() {
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

    public static <A> Iterable<A> drop(final int count, final Iterable<A> source) {
        return () -> new Iterator<A>() {
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

    public static <A> Iterable<A> join(final Iterable<A> a, final Iterable<A> b) {
        return () -> new Iterator<A>() {
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

    public static <A, B> Iterable<B> multiply(final Iterable<A> sources, final From<A, Iterable<B>> multiplier) {
        return () -> new Iterator<B>() {
            final Iterator<A> sourceA = sources.iterator();
            Iterator<B> sourceB = null;

            @Override
            public boolean hasNext() {
                while ((sourceB == null || !sourceB.hasNext()) && sourceA.hasNext()) {
                    sourceB = multiplier.from(sourceA.next()).iterator();
                    if (sourceB.hasNext()) {
                        return true;
                    }
                }
                return sourceB != null && sourceB.hasNext();
            }

            @Override
            public B next() {
                return sourceB.next();
            }

            @Override
            public void remove() {
                throw new IllegalStateException();
            }
        };
    }

    public static <A, B> Iterable<B> from(final Iterable<A> source, final From<A, B> from) {

        return () -> {
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
        };
    }

    public static <A> Iterable<A> unique(Iterable<A> as) {
        final Set<A> uniques = new HashSet<>();
        return Iterables.filter(as, uniques::add);
    }

    public static <A> Iterable<A> filter(final Iterable<A> source, final Where<A> where) {
        return () -> new Iterator<A>() {
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

    public static <A> int size(Iterable<A> as) {
        int i = 0;
        for (A ignored : as) {
            i += 1;
        }
        return i;
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
        return () -> new Iterator<A>() {
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

    public static <A> Iterable<A> of(final Enumeration<A> source) {
        return () -> new Iterator<A>() {
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

    public static <A> Iterable<A> of(A source) {
        return Collections.singletonList(source);
    }

    public static <A> Iterable<A> of(A... source) {
        return Arrays.asList(source);
    }

    public static <A, B> Iterable<Pair<A, B>> parallel(final Iterable<A> as, final Iterable<B> bs) {
        return () -> new Iterator<Pair<A, B>>() {
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

    public static <A> Iterable<A> repeat(final A a, final int times) {
        return () -> new Iterator<A>() {
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

    public static <A> Iterable<A> repeat(final Iterable<A> as, final int times) {
        return () -> new Iterator<A>() {
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

    @SuppressWarnings("unchecked")
    public static <A> Iterable<A> empty() {
        return (Iterable<A>) EMPTY;
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
        Option<A> last = Option.empty();
        for (A a : as) {
            if (where.is(a)) {
                last = Option.of(a);
            }
        }
        return last;
    }

    public static <A> Iterable<A> join(Iterable<A> current, A joiner, Iterable<A> next) {
        return join(join(current, of(joiner)), next);
    }

    public static <A> Iterable<Iterable<A>> split(final Iterable<A> contents, final Where<A> splitter) {
        return () -> new Iterator<Iterable<A>>() {
            final Iterator<A> source = contents.iterator();
            List<A> segment;
            boolean isTrailingEnd;

            @Override
            public boolean hasNext() {
                segment = new ArrayList<>();
                while (source.hasNext()) {
                    A next = source.next();
                    if (splitter.is(next)) {
                        isTrailingEnd = true;
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

    public static <B, A> B combine(Iterable<A> contents, B initial, Combine<A, B> condenser) {
        B condensate = initial;
        for (A content : contents) {
            condensate = condenser.from(content, condensate);
        }
        return condensate;
    }

    public static <A> Iterable<A> takeWhile(final Iterable<A> contents, final Where<A> where) {
        return () -> new Iterator<A>() {
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

    public static <A> Iterable<A> dropWhile(final Iterable<A> contents, final Where<A> where) {
        return () -> {
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

    public static <A, B extends Iterable<A>> Iterable<A> join(final Iterable<B> nexts) {
        return () -> new Iterator<A>() {
            final Iterator<B> sources = nexts.iterator();
            Iterator<A> current = null;

            @Override
            public boolean hasNext() {
                while ((current == null || !current.hasNext())) {
                    if (!sources.hasNext()) {
                        return false;
                    }
                    current = sources.next().iterator();
                    if (current.hasNext()) {
                        return true;
                    }
                }
                return true;
            }

            @Override
            public A next() {
                return current.next();
            }

            @Override
            public void remove() {
                throw new IllegalStateException();
            }
        };
    }

    public static <A> Iterable<A> breadth(final Iterable<A> sources,
                                          final From<A, Iterable<A>> multiplier) {
        return () -> new Iterator<A>() {
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
                    currentLevel = multiplier.from(todo.remove(0)).iterator();
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

    public static <A> Iterable<List<A>> breadthPaths(final Iterable<A> sources,
                                                     final From<A, Iterable<A>> multiplier) {
        return () -> new Iterator<List<A>>() {
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

    public static <A> Iterable<A> depth(final Iterable<A> sources, final From<A, Iterable<A>> multiplier) {
        return () -> new Iterator<A>() {
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

    private static Iterator<Object> iterator() {
        return (Iterator<Object>) EMPTY_ITERATOR;
    }

    public static <A> Iterable<A> withNext(final Iterable<A> contents,
                                           final From<A, A> nextFromCurrent) {
        return multiply(contents, a -> () -> new Iterator<A>() {
            A nextA = a;

            @Override
            public boolean hasNext() {
                return nextA != null;
            }

            @Override
            public A next() {
                A tmp = nextA;
                nextA = nextFromCurrent.from(nextA);
                return tmp;
            }
        });
    }

    public static <A, B> Iterable<B> sizedContents(final Iterable<A> contents,
                                              final From<A, Integer> getSize,
                                              final From2<A, Integer, B> getItem) {
        return multiply(contents, a -> () -> new Iterator<B>() {
            int size = getSize.from(a);
            int current = 0;

            @Override
            public boolean hasNext() {
                return size > current;
            }

            @Override
            public B next() {
                return getItem.from(a, current++);
            }
        });
    }
}
