package me.alaneuler.calcite.ng.demo.agg;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

public class AggregateReduceMain extends CommonTableMain {
  public static void main(String[] args) {
    String sql = """
        SELECT name, stddev(age), avg(age) from pt_user group by name
        """;
    RelNode relNode = RelUtils.sqlToRel(sql);

    HepPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();
  }

  private static HepPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.AGGREGATE_REDUCE_FUNCTIONS);
    return new HepPlanner(builder.build());
  }
}
