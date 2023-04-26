package me.alaneuler.calcite.ng.demo.materialize.tpch;

import me.alaneuler.calcite.ng.demo.util.FileUtils;

import static me.alaneuler.calcite.ng.demo.materialize.tpch.TpchBaseMain.MvRewriteType.SPJG;
import static me.alaneuler.calcite.ng.demo.materialize.tpch.TpchBaseMain.MvRewriteType.SUBSTITUTION;

/**
 * 大宽表场景
 */
public class TpchDenormalizedTableMain extends TpchBaseMain {
  public static void main(String[] args) {
    String sql, mvSql;
    RoutineResult result;

    /// Query 5, inner join
    // Case 1. Materialize whole query
    sql = FileUtils.getFileContent("tpch/queries/5.sql");
    result = routine(SUBSTITUTION, sql, sql);
    printInfo("Query 5, inner join, case 1 mv-whole, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, sql);
    printInfo("Query 5, inner join, case 1 mv-whole, " + SPJG.name(), result);

    // Case 2. Materialize part of query: SPJG
    mvSql = FileUtils.getFileContent("tpch/mvs/5-1.sql");
    result = routine(SUBSTITUTION, sql, mvSql);
    printInfo("Query 5, inner join, case 2 mv-SPJG, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, mvSql);
    printInfo("Query 5, inner join, case 2 mv-SPJG, " + SPJG.name(), result);

    // Case 3. Materialize part of query: SPJ
    mvSql = FileUtils.getFileContent("tpch/mvs/5-2.sql");
    result = routine(SUBSTITUTION, sql, mvSql);
    printInfo("Query 5, inner join, case 3 mv-SPJ, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, mvSql);
    printInfo("Query 5, inner join, case 3 mv-SPJ, " + SPJG.name(), result);

    /// Query 25, left join
    // Case 1. Materialize whole query
    sql = FileUtils.getFileContent("tpch/queries/25.sql");
    result = routine(SUBSTITUTION, sql, sql);
    printInfo("Query 25, left join, case 1 mv-whole, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, sql);
    printInfo("Query 25, left join, case 1 mv-whole, " + SPJG.name(), result);

    // Case 2. Materialize SPJG
    mvSql = FileUtils.getFileContent("tpch/mvs/25-1.sql");
    result = routine(SUBSTITUTION, sql, mvSql);
    printInfo("Query 25, left join, case 2 mv-SPJG, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, mvSql);
    printInfo("Query 25, left join, case 2 mv-SPJG, " + SPJG.name(), result);

    // Case 3. Materialize SPJ
    mvSql = FileUtils.getFileContent("tpch/mvs/25-2.sql");
    result = routine(SUBSTITUTION, sql, mvSql);
    printInfo("Query 25, left join, case 3 mv-SPJ, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, mvSql);
    printInfo("Query 25, left join, case 3 mv-SPJ, " + SPJG.name(), result);
  }
}
