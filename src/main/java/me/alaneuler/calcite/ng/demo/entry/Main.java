package me.alaneuler.calcite.ng.demo.entry;

import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import me.alaneuler.calcite.ng.demo.util.RelNodeUtils;
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

public class Main {
  public static void main(String[] args) throws Exception {
    // String sql = "select /*+ INDEX(myDate) */ * from table1 where myDate >= '2022-09-25'";
    String sql = """
        SELECT pt_user.id, name, age, sum(price)
        FROM pt_user join pt_order ON pt_user.id = pt_order.user_id
        WHERE age >= 20 AND age <= 30
        GROUP BY pt_user.id, name, age
        ORDER BY pt_user.id
        """;
    prepare();

    Planner planner = PlannerPool.getPlanner();
    SqlNode sqlNode = planner.parse(sql);
    sqlNode = planner.validate(sqlNode);
    RelNode relNode = planner.rel(sqlNode).project();

    RelOptPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();
    RelNodeUtils.dump(relNode);
  }

  private static void prepare() {
    TableUtils.createTable(
        "",
        "pt_user",
        List.of(
            Pair.of("id", SqlTypeName.INTEGER),
            Pair.of("name", SqlTypeName.VARCHAR),
            Pair.of("age", SqlTypeName.INTEGER)
        )
    );

    TableUtils.createTable(
        "",
        "pt_order",
        List.of(
            Pair.of("id", SqlTypeName.INTEGER),
            Pair.of("user_id", SqlTypeName.INTEGER),
            Pair.of("goods", SqlTypeName.VARCHAR),
            Pair.of("price", SqlTypeName.DECIMAL)
        )
    );
  }

  private static RelOptPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.FILTER_INTO_JOIN);
    return new HepPlanner(builder.build());
  }
}
