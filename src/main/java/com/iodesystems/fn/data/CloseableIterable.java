package com.iodesystems.fn.data;

import java.io.Closeable;

public interface CloseableIterable<T> extends Closeable, Iterable<T> {}
