package me.alaneuler.calcite.ng.demo.materialize.simple;

import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelDisplayUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.RelOptRules;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;

public class SimpleMainUsingVolcano extends MaterializeBaseMain {
  public static void main(String[] args) {
    String sql =
        """
        SELECT col1, SUM(cnt) FROM (
          SELECT col1, TO_DATE(ts, 'YYYY-MM-DD') AS dt, COUNT(*) AS cnt
          FROM tbl
          GROUP BY col1, dt
        ) GROUP BY col1
        """;
    String mvTableName = "tbl_mv";
    String mvSql =
        """
        SELECT col1, TO_DATE(ts, 'YYYY-MM-DD') AS dt, COUNT(*) AS cnt
        FROM tbl
        GROUP BY col1, dt
        """;

    RelNode relNode = RelUtils.sqlToRel(sql);
    RelDisplayUtils.dump(relNode);
    RelOptMaterialization materialization =
        MaterializeUtils.createMaterialization(mvTableName, mvSql, relNode.getCluster(), false);
    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(relNode);
    planner.setTopDownOpt(true);
    planner.setNoneConventionHasInfiniteCost(false);
    RelOptRules.MATERIALIZATION_RULES.forEach(planner::addRule);
    planner.addMaterialization(materialization);

    planner.setRoot(relNode);
    RelNode after = planner.findBestExp();
    RelDisplayUtils.dump(after);
  }
}
