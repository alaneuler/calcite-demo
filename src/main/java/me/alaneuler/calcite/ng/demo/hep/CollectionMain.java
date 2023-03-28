package me.alaneuler.calcite.ng.demo.hep;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

import java.util.List;

public class CollectionMain extends CommonTableMain {
  public static void main(String[] args) throws Exception {
    String sql = """ 
        select * from pt_user
        """;

    RelNode relNode = RelUtils.sqlToRel(sql);
    RelOptPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();
    RelUtils.dump(relNode);
  }

  private static RelOptPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleCollection(
        List.of(
            CoreRules.FILTER_INTO_JOIN,
            CoreRules.AGGREGATE_MERGE
        ));
    return new HepPlanner(builder.build());
  }
}