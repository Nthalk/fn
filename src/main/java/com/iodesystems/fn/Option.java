package com.iodesystems.fn;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Option<T> implements Iterable<T> {

    private static final Empty<?> EMPTY = new Empty<Object>();
    private static final Where<Option<?>> IS_PRESENT = new Where<Option<?>>() {
        @Override
        public boolean is(Option<?> objects) {
            return objects.isPresent();
        }
    };

    private static final Where<Option<?>> IS_EMPTY = new Where<Option<?>>() {
        @Override
        public boolean is(Option<?> objects) {
            return objects.isEmpty();
        }
    };

    @SuppressWarnings("unchecked")
    public static <V> Where<Option<V>> wherePresent() {
        return (Where) IS_PRESENT;
    }

    @SuppressWarnings("unchecked")
    public static <V> Where<Option<V>> whereEmpty() {
        return (Where) IS_EMPTY;
    }

    public static <T> Iterable<T> unwrap(Iterable<Option<T>> options) {
        final Iterator<Option<T>> iterator = options.iterator();
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private Option<T> next;

                    public boolean hasNext() {
                        while (iterator.hasNext()) {
                            next = iterator.next();
                            if (next.isPresent()) {
                                return true;
                            }
                        }
                        return false;
                    }

                    public T next() {
                        return next.get();
                    }

                    @Override
                    public void remove() {
                        throw new IllegalStateException();
                    }
                };
            }
        };
    }

    public static <T> Option<T> of(T item) {
        if (item == null) {
            return empty();
        } else {
            return new Present<T>(item);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Empty<T> empty() {
        return (Empty<T>) EMPTY;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            boolean hasNext = true;

            public boolean hasNext() {
                if (hasNext) {
                    hasNext = false;
                    return true;
                } else {
                    return false;
                }
            }

            public T next() {
                return get();
            }

            @Override
            public void remove() {
                throw new IllegalStateException();
            }
        };
    }

    public abstract T get();

    @SuppressWarnings("SameParameterValue")
    public T get(T ifEmpty) {
        if (isEmpty()) {
            return ifEmpty;
        } else {
            return get();
        }
    }

    public abstract boolean isEmpty();

    public abstract boolean isPresent();

    public static class Empty<T> extends Option<T> {
        private static final Iterator<?> EMPTY_ITERATOR = new Iterator<Object>() {
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

        private Empty() {
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterator<T> iterator() {
            return (Iterator<T>) EMPTY_ITERATOR;
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            return obj == EMPTY;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public T get() {
            throw new NoSuchElementException();
        }


        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean isPresent() {
            return false;
        }
    }

    public static class Present<T> extends Option<T> {
        private final T thing;

        private Present(T thing) {
            this.thing = thing;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Present<?> present = (Present<?>) o;
            if (thing == present.thing) return true;
            if (thing != null) if (thing.equals(present.thing)) return true;
            return false;
        }

        @Override
        public int hashCode() {
            return thing != null ? thing.hashCode() : 0;
        }

        @Override
        public T get() {
            return thing;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean isPresent() {
            return true;
        }
    }

}
