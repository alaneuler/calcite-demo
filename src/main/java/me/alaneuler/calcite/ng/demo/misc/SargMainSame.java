package me.alaneuler.calcite.ng.demo.misc;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelDisplayUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

public class SargMainSame extends CommonTableMain {
  public static void main(String[] args) {
    String sql =
        """
        SELECT *
        FROM pt_user
        WHERE id >= 100
          AND id <= 100
        """;

    RelNode rel = RelUtils.sqlToRel(sql);

    RelOptPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(rel);
    rel = hepPlanner.findBestExp();
    System.out.println(RelDisplayUtils.toGraphvizString(rel));
  }

  private static RelOptPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.FILTER_TO_CALC);
    builder.addRuleInstance(CoreRules.CALC_REDUCE_EXPRESSIONS);
    return new HepPlanner(builder.build());
  }
}
