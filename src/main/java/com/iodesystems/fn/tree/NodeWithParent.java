package com.iodesystems.fn.tree;

public class NodeWithParent<T> {

  private final NodeWithParent<T> parent;
  private final T item;

  public NodeWithParent(T item) {
    this(null, item);
  }

  public NodeWithParent(NodeWithParent<T> parent, T item) {
    this.parent = parent;
    this.item = item;
  }

  public NodeWithParent<T> getParent() {
    return parent;
  }

  public T getItem() {
    return item;
  }
}
