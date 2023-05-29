package me.alaneuler.calcite.ng.demo.misc;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelDisplayUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.hep.HepMatchOrder;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

public class Main extends CommonTableMain {
  public static void main(String[] args) throws Exception {
    // String sql = "select /*+ INDEX(myDate) */ * from table1 where myDate >=
    // '2022-09-25'";
    // String sql = "select * from pt_user";
    String sql =
        """
        SELECT pt_user.id, name, age, sum(price)
        FROM pt_user join pt_order ON pt_user.id = pt_order.user_id
        WHERE age >= 20 AND age <= 30
        GROUP BY pt_user.id, name, age
        ORDER BY pt_user.id
        """;

    RelNode relNode = RelUtils.sqlToRel(sql);
    RelOptPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();
    RelDisplayUtils.dump(relNode);
  }

  private static RelOptPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addMatchOrder(HepMatchOrder.BOTTOM_UP);
    builder.addRuleInstance(CoreRules.FILTER_INTO_JOIN);
    return new HepPlanner(builder.build());
  }
}
