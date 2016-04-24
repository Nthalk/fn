package com.iodesystems.fn.tree;

import com.iodesystems.fn.data.From;

import java.util.List;

public interface Adapter<NODE> extends From<NODE, Iterable<NODE>> {
    List<NODE> from(NODE node);

    Object attribute(NODE node, String attribute);

    NODE rebuild(NODE node, List<NODE> newChildren);
}
