package me.alaneuler.calcite.ng.demo;

import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import me.alaneuler.calcite.ng.demo.util.TableUtils;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.util.Pair;

import java.util.List;

public class Main {
  public static void main(String[] args) throws Exception {
    String sql = "select * from table1 where myDate >= TIMESTAMP ";
    prepare();

    Planner planner = PlannerPool.getPlanner();
    SqlNode sqlNode = planner.parse(sql);
    sqlNode = planner.validate(sqlNode);
    RelNode relNode = planner.rel(sqlNode).project();
    // planner.transform()
  }

  private static void prepare() {
    TableUtils.createTable("", "table1", List.of(Pair.of("myDate", SqlTypeName.TIMESTAMP)));
  }
}
