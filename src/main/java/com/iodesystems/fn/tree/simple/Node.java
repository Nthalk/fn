package com.iodesystems.fn.tree.simple;

import java.util.Arrays;
import java.util.List;

public class Node<T> {
    private final List<Node<?>> children;
    private final T value;

    public Node(T value, Node<?>... children) {
        this(value, Arrays.asList(children));
    }

    public Node(T value, List<Node<?>> children) {
        this.children = children;
        this.value = value;
    }

    public static <T> Node<T> v(T value, List<Node<?>> children) {
        return new Node<T>(value, children);
    }

    public static <T> Node<T> v(T value, Node<?>... children) {
        return new Node<T>(value, children);
    }

    public List<Node<?>> getChildren() {
        return children;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return children != null ? children.equals(node.children) : node.children == null && (value != null ? value.equals(node.value) : node.value == null);

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
