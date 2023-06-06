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

public class Main3Tables extends MaterializeBaseMain {
  public static void main(String[] args) {
    String mvSql =
        """
        SELECT
          customers.name,
          customers.age,
          products.name,
          orders.dt
        FROM orders, customers, products
        WHERE
          orders.product_id = products.id
          AND orders.customer_id = customers.id
          AND customers.age > 10
        """;
    String sql =
        """
        SELECT
          customers.name,
          customers.age,
          products.name,
          orders.dt
        FROM orders, customers, products
        WHERE
          orders.product_id = products.id
          AND orders.customer_id = customers.id
          AND customers.age > 10
          AND orders.dt > '2077.01.01'
        """;

    RelNode rel = RelUtils.sqlToRel(sql, Map.of("materializationsEnabled", "false"));
    RelDisplayUtils.dump(rel);

    RelOptMaterialization materialization =
        MaterializeUtils.createMaterialization("mv", mvSql, rel.getCluster(), true);
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
