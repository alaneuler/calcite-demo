package me.alaneuler.calcite.ng.demo.util;

import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.Planner;

public class RelUtils {
  public static void dump(RelNode root) {
    System.out.println(dumpStr(root));
  }

  public static String dumpStr(RelNode root) {
    StringBuilder sb = new StringBuilder(String.format("digraph %s {\n  \"rankdir\"=\"BT\";\n", root.getClass().getSimpleName()));
    dump(root, sb);
    return sb.append("}").toString();
  }

  public static VolcanoPlanner extractVolcanoPlanner(RelNode relNode) {
    return (VolcanoPlanner) relNode.getCluster().getPlanner();
  }

  private static void dump(RelNode node, StringBuilder sb) {
    for (RelNode child: node.getInputs()) {
      sb.append("  \"")
          .append(graphDigest(child))
          .append("\" -> \"")
          .append(graphDigest(node))
          .append("\";\n");
      dump(child, sb);
    }
  }

  /**
   * TODO: maybe we need a cache?
   */
  private static String graphDigest(RelNode relNode) {
    GraphvizWriter writer = new GraphvizWriter();
    relNode.explain(writer);
    return writer.getResult();
  }

  public static RelNode toRel(String sql) {
    try {
      Planner planner = PlannerPool.getPlanner();
      SqlNode sqlNode = planner.parse(sql);
      sqlNode = planner.validate(sqlNode);
      return planner.rel(sqlNode).project();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
