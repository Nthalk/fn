package com.iodesystems.fn.tree;

import com.iodesystems.fn.Fn;
import com.iodesystems.fn.Tr;
import com.iodesystems.fn.logic.Condition;
import com.iodesystems.fn.tree.simple.Node;
import org.junit.Test;

import java.util.List;

import static com.iodesystems.fn.tree.simple.Node.v;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TreeTests {

    @Test
    public void testTree() {
        Node root = v(1,
                      v(2,
                        v(3),
                        v(4),
                        v(5, v(6))),
                      v(7));

        Tr<Node> tr = Tr.of(root, Node.Adapter);
        List<Node> found = tr
            .find(Condition.of(Node.valueIs(4)).or(Node.valueIs(5)))
            .contents()
            .toList();
        assertTrue(found.size() == 2);
        assertTrue(Fn.eq(found.get(0).getValue(), 4));
        assertTrue(Fn.eq(found.get(1).getValue(), 5));

        List<Node> foundByPath = tr
            .findByPath(Fn.of(Node.valueIs(1),
                              Node.valueIs(2),
                              Condition.of(Node.valueIs(6)).or(Node.valueIs(5))))
            .contents()
            .toList();
        assertTrue(foundByPath.size() == 2);
        assertTrue(Fn.eq(foundByPath.get(0).getValue(), 5));
        assertTrue(Fn.eq(foundByPath.get(1).getValue(), 6));
    }

    @Test
    public void testPathGeneration() {
        Node root = v(1,
                      v(2,
                        v(3),
                        v(4),
                        v(5, v(6))),
                      v(7));

        Fn<Node> fnRoot = Fn.of(root, v(8));
        Fn<Node> allNodes = fnRoot.breadth(Node.Adapter);

        List<List<Node>> paths = fnRoot
            .breadthPaths(Node.Adapter)
            .toList();

        assertEquals(8, paths.size());
        assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(1))),
                     paths.get(0));

        assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(8))),
                     paths.get(1));

        assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(1)),
                             allNodes.firstOrNull(Node.valueIs(2))),
                     paths.get(2));

        assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(1)),
                             allNodes.firstOrNull(Node.valueIs(7))),
                     paths.get(3));

        assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(1)),
                             allNodes.firstOrNull(Node.valueIs(2)),
                             allNodes.firstOrNull(Node.valueIs(3))),
                     paths.get(4));

        assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(1)),
                             allNodes.firstOrNull(Node.valueIs(2)),
                             allNodes.firstOrNull(Node.valueIs(4))),
                     paths.get(5));

        assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(1)),
                             allNodes.firstOrNull(Node.valueIs(2)),
                             allNodes.firstOrNull(Node.valueIs(5))),
                     paths.get(6));

        assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(1)),
                             allNodes.firstOrNull(Node.valueIs(2)),
                             allNodes.firstOrNull(Node.valueIs(5)),
                             allNodes.firstOrNull(Node.valueIs(6))),
                     paths.get(7));
    }
}
