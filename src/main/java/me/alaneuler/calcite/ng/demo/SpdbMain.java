package me.alaneuler.calcite.ng.demo;

import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import me.alaneuler.calcite.ng.demo.util.TableUtils;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.util.Pair;

import java.util.List;

public class SpdbMain {
  public static void main(String[] args) throws Exception {
    String sql = """
        select khzh, kmhao, sum(lsh) from pt_spdb
        group by khzh, kmhao
        order by sum(lsh)
        """;
    prepare();

    Planner planner = PlannerPool.getPlanner();
    SqlNode sqlNode = planner.parse(sql);
    sqlNode = planner.validate(sqlNode);
    RelNode relNode = planner.rel(sqlNode).project();

    RelOptPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();
  }

  private static void prepare() {
    TableUtils.createTable(
        "",
        "pt_spdb",
        List.of(
            Pair.of("khzh", SqlTypeName.INTEGER),
            Pair.of("kmhao", SqlTypeName.VARCHAR),
            Pair.of("lsh", SqlTypeName.INTEGER)
        )
    );
  }

  private static RelOptPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.AGGREGATE_REMOVE);
    return new HepPlanner(builder.build());
  }
}
