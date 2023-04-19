package me.alaneuler.calcite.ng.demo.materialize.tpch;

import me.alaneuler.calcite.ng.demo.util.FileUtils;

import static me.alaneuler.calcite.ng.demo.materialize.tpch.TpchBaseMain.MvRewriteType.SPJG;
import static me.alaneuler.calcite.ng.demo.materialize.tpch.TpchBaseMain.MvRewriteType.SUBSTITUTION;

/**
 * 大宽表场景
 */
public class TpchDenormalizedTable extends TpchBaseMain {
  public static void main(String[] args) {
    /// Query 5, inner join
    // Case 1. Materialize whole query
    String sql = FileUtils.getFileContent("tpch/queries/5.sql");
    var result = routine(sql, sql, SUBSTITUTION);
    printInfo("Query 5, inner join, case 1 mv-whole, " + SUBSTITUTION.name(),
        result);
    result = routine(sql, sql, SPJG);
    printInfo("Query 5, inner join, case 1 mv-whole, " + SPJG.name(), result);

    // Case 2. Materialize part of query: SPJG
    String mvSql = FileUtils.getFileContent("tpch/mv/5-1.sql");
    result = routine(sql, mvSql, SUBSTITUTION);
    printInfo("Query 5, inner join, case 2 mv-SPJG, " + SUBSTITUTION.name(),
        result);
    result = routine(sql, mvSql, SPJG);
    printInfo("Query 5, inner join, case 2 mv-SPJG, " + SPJG.name(), result);

    // Case 3. Materialize part of query: SPJ
    mvSql = FileUtils.getFileContent("tpch/mv/5-2.sql");
    result = routine(sql, mvSql, SUBSTITUTION);
    printInfo("Query 5, inner join, case 3 mv-SPJ, " + SUBSTITUTION.name(),
        result);
    result = routine(sql, mvSql, SPJG);
    printInfo("Query 5, inner join, case 3 mv-SPJ, " + SPJG.name(), result);

    /// Query 25, left join
    // Case 1. Materialize whole query
    sql = FileUtils.getFileContent("tpch/queries/25.sql");
    result = routine(sql, sql, SUBSTITUTION);
    printInfo("Query 25, left join, case 1 mv-whole, " + SUBSTITUTION.name(),
        result);
    result = routine(sql, sql, SPJG);
    printInfo("Query 25, left join, case 1 mv-whole, " + SPJG.name(), result);

    // Case 2. Materialize SPJG
    mvSql = FileUtils.getFileContent("tpch/mv/25-1.sql");
    result = routine(sql, mvSql, SUBSTITUTION);
    printInfo("Query 25, left join, case 2 mv-SPJG, " + SUBSTITUTION.name(),
        result);
    result = routine(sql, mvSql, SPJG);
    printInfo("Query 25, left join, case 2 mv-SPJG, " + SPJG.name(), result);

    // Case 3. Materialize SPJ
    mvSql = FileUtils.getFileContent("tpch/mv/25-2.sql");
    result = routine(sql, mvSql, SUBSTITUTION);
    printInfo("Query 25, left join, case 3 mv-SPJ, " + SUBSTITUTION.name(),
        result);
    result = routine(sql, mvSql, SPJG);
    printInfo("Query 25, left join, case 3 mv-SPJ, " + SPJG.name(), result);
  }

  private static void printInfo(String banner, RoutineResult result) {
    System.out.println("### " + banner + " ###");
    System.out.println("### Before:");
    System.out.print(result.getBefore().explain());
    boolean success = result.getAfter().explain().contains("table=[[mv]]");
    if (success) {
      System.out.println("### After:");
      System.out.print(result.getAfter().explain());
    }

    System.out.println(
        result.getType() + " rewrite " + (success ? "succeed" : "failed"));
    System.out.println();
  }
}
