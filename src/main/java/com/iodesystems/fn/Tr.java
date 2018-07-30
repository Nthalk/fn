package com.iodesystems.fn;

import com.iodesystems.fn.data.From;
import com.iodesystems.fn.logic.Where;

public class Tr<NODE> {

  private final Fn<NODE> root;
  private final From<NODE, Iterable<NODE>> adapter;

  public Tr(Fn<NODE> root, From<NODE, Iterable<NODE>> adapter) {
    this.root = root;
    this.adapter = adapter;
  }

  public static <NODE> Tr<NODE> of(NODE root, From<NODE, Iterable<NODE>> adapter) {
    return new Tr<>(Fn.of(root), adapter);
  }

  public static <NODE> Tr<NODE> of(Iterable<NODE> root, From<NODE, Iterable<NODE>> adapter) {
    return new Tr<>(Fn.of(root), adapter);
  }

  public Fn<NODE> contents() {
    return root;
  }

  public Tr<NODE> find(Where<NODE> where) {
    return new Tr<>(root.breadth(adapter).where(where), adapter);
  }

  public Tr<NODE> findByPath(final Iterable<Where<NODE>> where) {
    Fn<NODE> current = root;
    int runs = 0;
    for (Where<NODE> check : where) {
      current = current.breadth(adapter).where(check);
      runs++;
    }

    if (runs == 0) {
      return new Tr<>(Fn.none(), adapter);
    }

    return new Tr<>(current, adapter);
  }

  @Override
  public String toString() {
    return "Tr(" + root.toString() + ")";
  }
}
