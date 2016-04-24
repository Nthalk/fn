package com.iodesystems.fn.tree.simple;

import com.iodesystems.fn.tree.Adapter;

import java.util.List;
import java.util.Map;

public class NodeAdapter implements Adapter<Node> {
    @Override
    public List<Node> from(Node node) {
        return node.getChildren();
    }

    @Override
    public Object attribute(Node node, String attribute) {
        if ("value".equals(attribute)) {
            return node.getValue();
        } else if ("type".equals(attribute)) {
            return node.getValue().getClass().getSimpleName();
        } else if (node.getValue() instanceof Map) {
            return ((Map) node.getValue()).get(attribute);
        }
        return null;
    }

    @Override
    public Node rebuild(Node node, List<Node> newChildren) {
        return Node.v(node.getValue(), newChildren);
    }

}
