package com.iodesystems.fn.tree;

import static com.iodesystems.fn.tree.simple.Node.v;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.iodesystems.fn.Fn;
import com.iodesystems.fn.Tr;
import com.iodesystems.fn.logic.Condition;
import com.iodesystems.fn.tree.simple.Node;
import java.util.List;
import org.junit.Test;

public class TreeTests {

  @Test
  public void testTree() {
    Node root = v(1, v(2, v(3), v(4), v(5, v(6))), v(7));

    Tr<Node> tr = Tr.of(root, Node.Adapter);
    List<Node> found =
        tr.find(Condition.of(Node.valueIs(4)).or(Node.valueIs(5))).contents().toList();
    assertEquals(2, found.size());
    assertTrue(Fn.eq(found.get(0).getValue(), 4));
    assertTrue(Fn.eq(found.get(1).getValue(), 5));

    List<Node> foundByPath =
        tr.findByPath(
            Fn.of(
                Node.valueIs(1),
                Node.valueIs(2),
                Condition.of(Node.valueIs(6)).or(Node.valueIs(5))))
            .contents()
            .toList();
    assertEquals(2, foundByPath.size());
    assertTrue(Fn.eq(foundByPath.get(0).getValue(), 5));
    assertTrue(Fn.eq(foundByPath.get(1).getValue(), 6));
  }

  @Test
  public void testPathGeneration() {
    Node root = v(1, v(2, v(3), v(4), v(5, v(6))), v(7));

    Fn<Node> fnRoot = Fn.of(root, v(8));
    Fn<Node> allNodes = fnRoot.breadth(Node.Adapter);

    Fn<List<Node>> paths = fnRoot.breadthPaths(Node.Adapter);

    assertEquals(8, paths.size());
    assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(1))), paths.getOrElse(0));

    assertEquals(Fn.list(allNodes.firstOrNull(Node.valueIs(8))), paths.getOrElse(1));

    assertEquals(
        Fn.list(allNodes.firstOrNull(Node.valueIs(1)), allNodes.firstOrNull(Node.valueIs(2))),
        paths.getOrElse(2));

    assertEquals(
        Fn.list(allNodes.firstOrNull(Node.valueIs(1)), allNodes.firstOrNull(Node.valueIs(7))),
        paths.getOrElse(3));

    assertEquals(
        Fn.list(
            allNodes.firstOrNull(Node.valueIs(1)),
            allNodes.firstOrNull(Node.valueIs(2)),
            allNodes.firstOrNull(Node.valueIs(3))),
        paths.getOrElse(4));

    assertEquals(
        Fn.list(
            allNodes.firstOrNull(Node.valueIs(1)),
            allNodes.firstOrNull(Node.valueIs(2)),
            allNodes.firstOrNull(Node.valueIs(4))),
        paths.getOrElse(5));

    assertEquals(
        Fn.list(
            allNodes.firstOrNull(Node.valueIs(1)),
            allNodes.firstOrNull(Node.valueIs(2)),
            allNodes.firstOrNull(Node.valueIs(5))),
        paths.getOrElse(6));

    assertEquals(
        Fn.list(
            allNodes.firstOrNull(Node.valueIs(1)),
            allNodes.firstOrNull(Node.valueIs(2)),
            allNodes.firstOrNull(Node.valueIs(5)),
            allNodes.firstOrNull(Node.valueIs(6))),
        paths.getOrElse(7));
  }
}
