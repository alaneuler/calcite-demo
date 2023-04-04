package me.alaneuler.calcite.ng.demo.entry;

import me.alaneuler.calcite.ng.demo.config.SchemaConfig;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.SqlUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;

public class AggRemoveMain {
  public static void main(String[] args) throws Exception {
    String sql = """
        SELECT name, sum(count_inner)
        FROM
          (SELECT name, COUNT(*) AS count_inner
           FROM pt_lsb
           GROUP BY name
           ORDER BY count_inner LIMIT 100)
        GROUP BY name
        """;
    prepare();

    RelNode relNode = RelUtils.sqlToRel(sql);

    VolcanoPlanner volcanoPlanner = VolcanoUtils.extractVolcanoPlanner(relNode);
    volcanoPlanner.setNoneConventionHasInfiniteCost(false);
    volcanoPlanner.setRoot(relNode);
    relNode = volcanoPlanner.findBestExp();
    VolcanoUtils.dump(volcanoPlanner);
    System.out.println(SqlUtils.toSqlString(relNode));
  }

  private static void prepare() {
    SchemaConfig.addTable(
        "CREATE TABLE IF NOT EXISTS `pt_lsb` (name VARCHAR NOT NULL)");
  }
}
