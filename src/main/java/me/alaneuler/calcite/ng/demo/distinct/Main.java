package me.alaneuler.calcite.ng.demo.distinct;

import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.SqlUtils;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

public class Main extends DistinctTableMain {
  public static void main(String[] args) {
    String sql =
        """
        SELECT
          age,
          country,
          COUNT(DISTINCT first_name),
          COUNT(DISTINCT last_name),
          COUNT(DISTINCT state),
          AVG(signupDate),
          SUM(id)
        FROM
          pt_user
        GROUP BY
          age, country
        """;

    RelNode rel = RelUtils.sqlToRel(sql);
    System.out.println(rel.explain());

    HepPlanner planner = hepPlanner();
    planner.setRoot(rel);
    RelNode after = planner.findBestExp();
    System.out.println(after.explain());
    System.out.println(SqlUtils.toSqlString(after));
  }

  private static HepPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.AGGREGATE_EXPAND_DISTINCT_AGGREGATES_TO_JOIN);
    return new HepPlanner(builder.build());
  }
}
