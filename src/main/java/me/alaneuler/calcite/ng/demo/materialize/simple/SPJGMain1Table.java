package me.alaneuler.calcite.ng.demo.materialize.simple;

import java.util.Map;
import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelDisplayUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.RelOptRules;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;

public class SPJGMain1Table extends MaterializeBaseMain {
  public static void main(String[] args) {
    String sql = """
        SELECT * FROM products
        WHERE price > 10
        """;
    String mvSql = """
        SELECT * FROM products
        WHERE price > 10
        """;

    RelNode rel = RelUtils.sqlToRel(sql, Map.of("materializationsEnabled", "false"));
    RelDisplayUtils.dump(rel);

    RelOptMaterialization materialization =
        MaterializeUtils.createMaterialization("SPJGMain1TableMv", mvSql, rel.getCluster(), true);
    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(rel);
    planner.setTopDownOpt(true);
    planner.setNoneConventionHasInfiniteCost(false);
    RelOptRules.MATERIALIZATION_RULES.forEach(planner::addRule);
    planner.addMaterialization(materialization);

    planner.setRoot(rel);
    RelNode after = planner.findBestExp();
    RelDisplayUtils.dump(after);
  }
}
