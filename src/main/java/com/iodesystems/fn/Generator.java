package com.iodesystems.fn;

public interface Generator<T> {
    T next();

    abstract class Callable<T> implements Generator {
        private final java.util.concurrent.Callable<T> callable;

        public Callable(java.util.concurrent.Callable<T> callable) {
            this.callable = callable;
        }

        @Override
        public T next() {
            try {
                return callable.call();
            } catch (Exception e) {
                // Uhoh...
                return null;
            }
        }
    }
}
