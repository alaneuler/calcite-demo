package me.alaneuler.calcite.ng.demo.util;

import java.lang.reflect.Field;
import java.util.Map;
import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.Planner;

public class RelUtils {
  public static LogicalTableScan createTableScan(String tableName) {
    RelNode relNode = sqlToRel("SELECT * FROM " + tableName);
    return (LogicalTableScan) relNode.getInput(0);
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

  public static RelNode sqlToRel(String sql, Map<String, String> configs) {
    try {
      Planner planner = PlannerPool.getPlanner(configs);
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

    for (RelNode input : relNode.getInputs()) {
      setCluster(input, cluster);
    }
  }
}
