package me.alaneuler.calcite.ng.demo;

import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import me.alaneuler.calcite.ng.demo.config.SchemaConfig;
import me.alaneuler.calcite.ng.demo.util.SqlUtils;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.Planner;

public class AggRemoveMain {
  public static void main(String[] args) throws Exception {
    String sql = """
        SELECT kmhao,
               sum(count_lsh) AS count_lsh_all
        FROM (
                (SELECT kmhao,
                        COUNT(lsh) AS count_lsh
                 FROM pt_lsb
                 GROUP BY kmhao
                 ORDER BY count_lsh DESC LIMIT 100)
              UNION ALL
                (SELECT kmhao,
                        COUNT(lsh) AS count_lsh
                 FROM pt_lsb
                 GROUP BY kmhao
                 ORDER BY count_lsh DESC LIMIT 100)
              UNION ALL
                (SELECT kmhao,
                        COUNT(lsh) AS count_lsh
                 FROM pt_lsb
                 GROUP BY kmhao
                 ORDER BY count_lsh DESC LIMIT 100) )
        GROUP BY kmhao
        ORDER BY count_lsh_all DESC LIMIT 100
        """;
    prepare();

    Planner planner = PlannerPool.getPlanner();
    SqlNode sqlNode = planner.parse(sql);
    sqlNode = planner.validate(sqlNode);
    RelNode relNode = planner.rel(sqlNode).project();

    RelOptPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();
    System.out.println(SqlUtils.toSqlString(relNode));
  }

  private static void prepare() {
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `pt_lsb` (lsh VARCHAR NOT NULL, kmhao VARCHAR NOT NULL)");
  }

  private static RelOptPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.AGGREGATE_UNION_TRANSPOSE);
    builder.addRuleInstance(CoreRules.AGGREGATE_REMOVE);
    return new HepPlanner(builder.build());
  }
}
