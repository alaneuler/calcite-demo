package me.alaneuler.calcite.ng.demo.materialize;

import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.RelOptRules;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;

public class SimpleMainUsingHepPlanner extends MaterializeBaseMain {
  public static void main(String[] args) {
    String sql = """
        SELECT col1, SUM(cnt) FROM (
          SELECT col1, TO_DATE(ts, 'YYYY-MM-DD') AS dt, COUNT(*) AS cnt
          FROM tbl
          GROUP BY col1, dt
        ) GROUP BY col1
        """;
    String mvTableName = "tbl_mv";
    String mvSql = """
        SELECT col1, TO_DATE(ts, 'YYYY-MM-DD') AS dt, COUNT(*) AS cnt
        FROM tbl
        GROUP BY col1, dt
        """;

    RelNode relNode = RelUtils.sqlToRel(sql);
    RelUtils.dump(relNode);
    RelOptMaterialization materialization = MaterializeUtils.createMaterialization(mvTableName, mvSql, relNode.getCluster());
    HepPlanner planner = hepPlanner();
    RelOptRules.MATERIALIZATION_RULES.forEach(planner::addRule);
    planner.addMaterialization(materialization);

    planner.setRoot(relNode);
    RelNode after = planner.findBestExp();
    RelUtils.dump(after);
  }

  private static HepPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    RelOptRules.MATERIALIZATION_RULES.forEach(builder::addRuleInstance);
    return new HepPlanner(builder.build());
  }
}
