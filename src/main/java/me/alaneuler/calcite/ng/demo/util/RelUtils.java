package me.alaneuler.calcite.ng.demo.util;

import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.Planner;

import java.lang.reflect.Field;

public class RelUtils {
  public static LogicalTableScan createTableScan(String tableName) {
    RelNode relNode = sqlToRel("SELECT * FROM " + tableName);
    return (LogicalTableScan) relNode.getInput(0);
  }

  public static void dump(RelNode root) {
    System.out.println(toGraphvizString(root));
  }

  public static String toGraphvizString(RelNode root) {
    StringBuilder sb = new StringBuilder(String.format("digraph %s {\n  \"rankdir\"=\"BT\";\n", root.getClass().getSimpleName()));
    dump(root, sb);
    return sb.append("}").toString();
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

  private static String graphDigest(RelNode relNode) {
    GraphvizWriter writer = new GraphvizWriter();
    relNode.explain(writer);
    return writer.getResult();
  }

  public static RelNode sqlToRel(String sql) {
    try {
      Planner planner = PlannerPool.getPlanner();
      SqlNode sqlNode = planner.parse(sql);
      sqlNode = planner.validate(sqlNode);
      return planner.rel(sqlNode).project();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static void setCluster(RelNode relNode, RelOptCluster cluster) {
    Field field = ReflectionUtils.getField(relNode, "cluster");
    ReflectionUtils.setField(relNode, field, cluster);
  }
}
