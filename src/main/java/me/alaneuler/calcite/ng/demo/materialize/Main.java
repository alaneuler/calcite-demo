package me.alaneuler.calcite.ng.demo.materialize;

import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.RelOptRules;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;

public class Main extends MaterializeBaseMain {

  public static void main(String[] args) {
    String sql = """
        SELECT col1, COUNT(*)
        FROM tbl
        WHERE ts > '2023-01-01 00:00:00' AND ts < '2023-01-10 23:59:59'
        GROUP BY col1
        """;
    String mvTableName = "tbl_mv";
    String mvSql = """
        SELECT col1, TO_DATE(ts, 'YYYY-MM-DD') AS dt, COUNT(*) AS cnt
        FROM tbl
        GROUP BY col1, dt
        """;

    RelOptMaterialization materialization = MaterializeUtils.createMaterialization(mvTableName, mvSql, null);
    RelNode relNode = RelUtils.sqlToRel(sql);
    HepPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();
  }

  private static HepPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    RelOptRules.MATERIALIZATION_RULES.forEach(builder::addRuleInstance);
    return new HepPlanner(builder.build());
  }
}
