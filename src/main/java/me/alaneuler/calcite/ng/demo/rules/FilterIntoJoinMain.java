package me.alaneuler.calcite.ng.demo.rules;

import java.util.Map;
import me.alaneuler.calcite.ng.demo.materialize.spjg.MaterializeBaseMain;
import me.alaneuler.calcite.ng.demo.util.RelDisplayUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

public class FilterIntoJoinMain extends MaterializeBaseMain {
  public static void main(String[] args) {
    String sql =
        """
        SELECT *
        FROM orders,
             products,
             customers
        WHERE orders.product_id = products.id
          AND orders.customer_id = customers.id
            """;

    RelNode rel = RelUtils.sqlToRel(sql, Map.of("materializationsEnabled", "false"));
    RelDisplayUtils.dump(rel);

    RelOptPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(rel);
    RelNode after = hepPlanner.findBestExp();
    RelDisplayUtils.dump(after);
  }

  private static RelOptPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.FILTER_INTO_JOIN);
    return new HepPlanner(builder.build());
  }
}
