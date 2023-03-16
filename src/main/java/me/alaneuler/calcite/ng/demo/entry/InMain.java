package me.alaneuler.calcite.ng.demo.entry;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.SqlUtils;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

public class InMain extends CommonTableMain {
  public static void main(String[] args) throws Exception {
    // String sql = "select /*+ INDEX(myDate) */ * from table1 where myDate >= '2022-09-25'";
    String sql = """
        select * from pt_user
        where id in (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21)
        """;

    RelNode relNode = RelUtils.sqlToRel(sql);

    RelOptPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();
    System.out.println(SqlUtils.toSqlString(relNode));
  }

  private static RelOptPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.FILTER_INTO_JOIN);
    return new HepPlanner(builder.build());
  }
}
