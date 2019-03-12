package com.iodesystems.fn.tree.simple;

import com.iodesystems.fn.tree.Adapter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Node {

  public static final Adapter<Node> Adapter = new NodeAdapter();
  protected final List<Node> children;
  private final Object value;

  public Node(Object value, Node... children) {
    this(value, Arrays.asList(children));
  }

  public Node(Object value, List<Node> children) {
    this.children = children;
    this.value = value;
  }

  public static Node v(Object value, List<Node> children) {
    return new Node(value, children);
  }

  public static Node v(Object value, Node... children) {
    return new Node(value, children);
  }

  public List<Node> getChildren() {
    return children;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Node node = (Node) o;

    if (!Objects.equals(children, node.children)) {
      return false;
    }
    return Objects.equals(value, node.value);
  }

  @Override
  public int hashCode() {
    int result = children != null ? children.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    if (!children.isEmpty()) {
      StringBuilder childString = new StringBuilder();
      for (Node child : children) {
        childString.append(", ");
        childString.append(child);
      }
      return "v(" + value + childString.toString() + ")";
    } else {
      return "v(" + value + ")";
    }
  }
}
