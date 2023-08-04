package me.alaneuler.calcite.ng.demo.materialize.spjg;

import java.util.Map;
import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelDisplayUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.RelOptRules;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;

public class QueryMoreThanViewJoinNormal extends MaterializeBaseMain {
  public static void main(String[] args) {
    String mvSql =
        """
        SELECT *
        FROM orders
        JOIN customers ON orders.customer_id = customers.id
        """;

    String sql =
        """
        SELECT *
        FROM orders
        JOIN products ON orders.product_id = products.id
        JOIN customers ON orders.customer_id = customers.id
        """;

    RelNode rel = RelUtils.sqlToRel(sql, Map.of("materializationsEnabled", "false"));
    RelDisplayUtils.dump(rel);

    RelOptMaterialization materialization =
        MaterializeUtils.createMaterialization(
            "QueryMoreThanViewMv", mvSql, rel.getCluster(), true);

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
