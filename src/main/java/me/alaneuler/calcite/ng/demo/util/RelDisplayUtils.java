package me.alaneuler.calcite.ng.demo.util;

import org.apache.calcite.plan.volcano.RelSubset;
import org.apache.calcite.rel.RelNode;

public class RelDisplayUtils {
  public static String toGraphvizString(RelNode root) {
    StringBuilder sb =
        new StringBuilder(
            String.format(
                "digraph %s {\n  \"rankdir\"=\"BT\";\n", root.getClass().getSimpleName()));
    dump(root, sb);
    return sb.append("}").toString();
  }

  public static void dump(RelNode root) {
    System.out.println(toGraphvizString(root));
  }

  private static void dump(RelNode node, StringBuilder sb) {
    if (node instanceof RelSubset relSubset) {
      node = relSubset.getBest();
    }

    for (RelNode child : node.getInputs()) {
      sb.append("  \"")
          .append(graphDigest(child))
          .append("\" -> \"")
          .append(graphDigest(node))
          .append("\";\n");
      dump(child, sb);
    }
  }

  private static String graphDigest(RelNode relNode) {
    GraphvizWriter writer = new GraphvizWriter(true, false);
    relNode.explain(writer);
    return writer.getResult();
  }
}
