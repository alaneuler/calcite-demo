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

public class SPJGMain2Tables extends MaterializeBaseMain {
  public static void main(String[] args) {
    String sql =
        """
        SELECT t1.name, SUM(t1.price)
        FROM (
          SELECT products.name, products.price, orders.dt
          FROM orders, products
          WHERE orders.product_id = products.id
            AND (products.price > 10 OR products.price < 5)
        ) AS t1
        GROUP BY t1.name
        """;
    String mvSql =
        """
        SELECT products.name, products.price, orders.dt
        FROM orders, products
        WHERE orders.product_id = products.id
          AND (products.price > 10 OR products.price < 5)
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
