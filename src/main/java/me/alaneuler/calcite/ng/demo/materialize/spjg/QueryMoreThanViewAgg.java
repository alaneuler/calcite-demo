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

public class QueryMoreThanViewAgg extends MaterializeBaseMain {
  public static void main(String[] args) {
    String mvSql =
        """
        select name, sum(age) as total_age
        from customers
        group by name
        """;
    String sql =
        """
        select t1.name, sum(t1.age) as total_age, sum(t1.price) as total_price
        from (
          select * from products
          join customers
          on products.name = customers.name
        ) as t1
        group by t1.name
        """;

    RelNode rel = RelUtils.sqlToRel(sql, Map.of("materializationsEnabled", "false"));
    RelDisplayUtils.dump(rel);

    RelOptMaterialization materialization =
        MaterializeUtils.createMaterialization(
            "QueryMoreThanViewAggMv", mvSql, rel.getCluster(), true);

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
