package com.iodesystems.fn;


import java.util.*;

public abstract class Iterables {

    private static final Iterable<?> EMPTY = new Iterable<Object>() {
        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
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
        }
    };

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
                    final Iterator<A> next = b.iterator();
                    Iterator<A> current = a.iterator();
                    final Iterator<A> first = current;

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

    public static <A> Iterable<A> of(A source) {
        return Collections.singletonList(source);
    }

    public static <A> Iterable<A> of(A... source) {
        return Arrays.asList(source);
    }

    public static <A> Iterable<A> repeat(Iterable<A> as, int times) {
        Iterable<A> target = as;
        for (int i = 1; i < times; i++) {
            target = Iterables.join(target, as);
        }
        return target;
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
        return new Iterable<Iterable<A>>() {
            @Override
            public Iterator<Iterable<A>> iterator() {
                return new Iterator<Iterable<A>>() {
                    Iterator<A> source = contents.iterator();
                    List<A> segment;
                    boolean isTrailingEnd;

                    @Override
                    public boolean hasNext() {
                        segment = new ArrayList<A>();
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
        };

    }

    public static <B, A> B combine(Iterable<A> contents, B initial, Combine<A, B> condenser) {
        B condensate = initial;
        for (A content : contents) {
            condensate = condenser.from(content, condensate);
        }
        return condensate;
    }
}
