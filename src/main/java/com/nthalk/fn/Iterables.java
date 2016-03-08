package com.nthalk.fn;


import java.util.*;

public abstract class Iterables {

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

    public static <A> Iterable<A> join(final Iterable<A> a, final Iterable<A> b) {
        return new Iterable<A>() {
            public Iterator<A> iterator() {
                return new Iterator<A>() {
                    Iterator<A> current = a.iterator();
                    final Iterator<A> first = current;
                    final Iterator<A> next = b.iterator();

                    public boolean hasNext() {
                        if (current.hasNext()) {
                            return true;
                        } else if (current == first) {
                            current = next;
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

    public static <A, B> Iterable<B> multiply(final Iterable<A> sources, final From<A, Iterable<B>> multiplier) {
        return new Iterable<B>() {
            public Iterator<B> iterator() {
                return new Iterator<B>() {
                    final Iterator<A> parentSources = sources.iterator();
                    Iterator<B> currentSourceItems = null;

                    public boolean hasNext() {
                        if (currentSourceItems == null || !currentSourceItems.hasNext()) {
                            if (!parentSources.hasNext()) {
                                return false;
                            }
                            do {
                                currentSourceItems = multiplier.from(parentSources.next()).iterator();
                                if (currentSourceItems.hasNext()) {
                                    return true;
                                }
                            } while (parentSources.hasNext());
                        }
                        return currentSourceItems.hasNext();
                    }

                    public B next() {
                        return currentSourceItems.next();
                    }

                    @Override
                    public void remove() {
                        throw new IllegalStateException();
                    }
                };
            }
        };
    }

    public static <A, B> Iterable<B> from(final Iterable<A> source, final From<A, B> from) {

        return new Iterable<B>() {
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
        final Set<A> uniques = new HashSet<A>();
        return Iterables.filter(as, new Where<A>() {
            @Override
            public boolean is(A a) {
                return uniques.add(a);
            }
        });
    }

    private static <A> Iterable<A> filter(final Iterable<A> source, final Where<A> where) {
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

    public static <A> int size(Iterable<A> as) {
        int i = 0;
        for (A ignored : as) {
            i += 1;
        }
        return i;
    }

    public static <A> List<A> toList(Iterable<A> contents) {
        List<A> list = new ArrayList<A>();
        for (A content : contents) {
            list.add(content);
        }
        return list;
    }

    public static <A> Iterable<A> of(A... source) {
        return Arrays.asList(source);
    }
}
