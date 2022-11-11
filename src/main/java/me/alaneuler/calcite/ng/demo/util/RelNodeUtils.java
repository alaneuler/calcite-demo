package me.alaneuler.calcite.ng.demo.util;

import org.apache.calcite.rel.RelNode;

public class RelNodeUtils {
  public static String dump(RelNode root) {
    StringBuilder sb = new StringBuilder(String.format("digraph %s {\n", root.getClass().getSimpleName()));
    dump(root, sb);
    return sb.append("}").toString();
  }

  private static void dump(RelNode node, StringBuilder sb) {
    for (RelNode child: node.getInputs()) {
      sb.append("  \"")
          .append(graphDigest(node))
          .append("\" -> \"")
          .append(graphDigest(child))
          .append("\";\n");
      dump(child, sb);
    }
  }

  // TODO: maybe we need a cache?
  private static String graphDigest(RelNode relNode) {
    GraphvizWriter writer = new GraphvizWriter();
    relNode.explain(writer);
    return writer.getResult();
  }
}
