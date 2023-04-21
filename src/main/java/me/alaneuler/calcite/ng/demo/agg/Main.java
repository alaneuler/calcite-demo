package me.alaneuler.calcite.ng.demo.agg;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

public class Main extends CommonTableMain {
  public static void main(String[] args) {
    String sql = """
        SELECT user_id,
               goods,
               sum(price)
        FROM (
          SELECT user_id, goods, price
          FROM pt_order WHERE id < 100
          UNION ALL
          SELECT user_id, goods, price
          FROM pt_order WHERE id > 1000
        )
        GROUP BY user_id, goods
        """;

    RelNode relNode = RelUtils.toRel(sql);

    HepPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();

    RelUtils.dump(relNode);
  }

  private static HepPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.AGGREGATE_UNION_TRANSPOSE);
    return new HepPlanner(builder.build());
  }
}
