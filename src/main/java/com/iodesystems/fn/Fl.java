package com.iodesystems.fn;

import com.iodesystems.fn.thread.PipedProcessNode;
import com.iodesystems.fn.thread.PipedProcessNodeBuilder;

public class Fl<T> {
  private final PipedProcessNode<T, T> root = new PipedProcessNode<>(null, t -> t);

  public static <T> PipedProcessNodeBuilder<T, T, T> of(@SuppressWarnings("unused") Class<T> cls) {
    Fl<T> source = new Fl<>();
    return new PipedProcessNodeBuilder<>(source, source.root);
  }

  public void submit(T data) {
    root.submit(data);
  }
}
