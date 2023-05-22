package me.alaneuler.calcite.ng.demo.cost;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

public class Main extends CommonTableMain {
  public static void main(String[] args) {
    String sql =
        """
        SELECT
          name,
          AVG(age)
        FROM
          pt_user
        WHERE
          age > 10
        GROUP BY
          name
        """;

    RelNode relNode = RelUtils.sqlToRel(sql);
    RelUtils.dump(relNode);

    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(relNode);
    planner.setTopDownOpt(true);
    planner.setNoneConventionHasInfiniteCost(false);
    planner.addRule(CoreRules.AGGREGATE_PROJECT_MERGE);

    planner.setRoot(relNode);
    RelNode after = planner.findBestExp();
    RelUtils.dump(after);
  }
}
