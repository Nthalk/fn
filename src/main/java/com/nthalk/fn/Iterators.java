package com.nthalk.fn;


import java.util.Iterator;

public abstract class Iterators {

    public static <A> Iterable<A> of(final A... as) {
        return new Iterable<A>() {
            public Iterator<A> iterator() {
                return new Iterator<A>() {
                    int i = 0;

                    public boolean hasNext() {
                        return i < as.length;
                    }

                    public A next() {
                        return as[i++];
                    }

                    @Override
                    public void remove() {
                        throw new IllegalStateException();
                    }
                };
            }
        };
    }

    public static <A> Iterator<A> take(final int count, final Iterator<A> source) {
        return new Iterator<A>() {
            int myCount = count;

            public boolean hasNext() {
                return myCount-- > 0 && source.hasNext();
            }

            public A next() {
                return source.next();
            }

            @Override
            public void remove() {
                throw new IllegalStateException();
            }
        };
    }

    public static <A> Iterator<A> drop(int count, final Iterator<A> source) {
        for (int i = 0; i < count; i++) {
            source.hasNext();
        }
        return source;
    }

    public static <A> Iterable<A> join(final Iterable<A> a, final Iterable<A> b) {
        return join(a.iterator(), b.iterator());
    }

    public static <A> Iterable<A> join(final Iterator<A> a, final Iterator<A> b) {
        return new Iterable<A>() {
            public Iterator<A> iterator() {
                return new Iterator<A>() {
                    Iterator<A> current = a;

                    public boolean hasNext() {
                        if (current.hasNext()) {
                            return true;
                        } else if (current != a) {
                            current = b;
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

    public static <A> Option<A> combine(final Iterable<A> source, Merge<A, A, A> merger) {
        A initial = null;
        for (A a : source) {
            if (initial == null) {
                initial = a;
            } else {
                initial = merger.from(a, initial);
            }
        }
        return Option.of(initial);
    }

    public static <A> A combine(final Iterable<A> source, A initial, Merge<A, A, A> merger) {
        for (A a : source) {
            initial = merger.from(a, initial);
        }
        return initial;
    }

    public static <A, B> Iterable<B> multiply(A source, final From<A, Iterable<B>> multiplier) {
        return multiplier.from(source);
    }

    public static <A, B> Iterable<B> multiply(final Iterator<A> sources, final From<A, Iterable<B>> multiplier) {
        return new Iterable<B>() {
            public Iterator<B> iterator() {
                return new Iterator<B>() {
                    Iterator<B> currentSourceItems = null;

                    public boolean hasNext() {
                        if (currentSourceItems == null) {
                            if (!sources.hasNext()) {
                                return false;
                            }
                            do {
                                currentSourceItems = multiplier.from(sources.next()).iterator();
                                if (currentSourceItems.hasNext()) {
                                    return true;
                                }
                            } while (sources.hasNext());
                        }

                        if (currentSourceItems.hasNext()) {
                            return true;
                        } else {
                            do {
                                currentSourceItems = multiplier.from(sources.next()).iterator();
                                if (currentSourceItems.hasNext()) {
                                    return true;
                                }
                            } while (sources.hasNext());
                        }
                        return false;
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

    public static <A, B> Iterable<B> multiply(final Iterable<A> source, final From<A, Iterable<B>> multiplier) {
        final Iterator<A> sources = source.iterator();
        return multiply(sources, multiplier);
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
}
