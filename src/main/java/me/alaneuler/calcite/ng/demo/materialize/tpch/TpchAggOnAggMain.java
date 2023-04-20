package me.alaneuler.calcite.ng.demo.materialize.tpch;

import me.alaneuler.calcite.ng.demo.util.FileUtils;

public class TpchAggOnAggMain extends TpchBaseMain {
  public static void main(String[] args) {
    String sql, mvSql;
    RoutineResult result;

    /// Query 26, aggregate on aggregate
    // Case 1. Materialize whole query
    sql = FileUtils.getFileContent("tpch/queries/26.sql");
    result = routine(sql, sql, MvRewriteType.SUBSTITUTION);
    printInfo("Query 26, aggregate on aggregate, case 1 mv-whole, " + MvRewriteType.SUBSTITUTION.name(),
        result);
    result = routine(sql, sql, MvRewriteType.SPJG);
    printInfo("Query 26, aggregate on aggregate, case 1 mv-whole, " + MvRewriteType.SPJG.name(), result);

    // Case 2. Materialize agg on agg
    mvSql = FileUtils.getFileContent("tpch/mvs/26-1.sql");
    result = routine(sql, mvSql, MvRewriteType.SUBSTITUTION);
    printInfo("Query 26, aggregate on aggregate, case 2 mv-agg on agg, " + MvRewriteType.SUBSTITUTION.name(),
        result);
    result = routine(sql, mvSql, MvRewriteType.SPJG);
    printInfo("Query 26, aggregate on aggregate, case 2 mv-agg on agg, " + MvRewriteType.SPJG.name(), result);

    // Case 3. Materialize second agg
    mvSql = FileUtils.getFileContent("tpch/mvs/26-2.sql");
    result = routine(sql, mvSql, MvRewriteType.SUBSTITUTION);
    printInfo("Query 26, aggregate on aggregate, case 3 mv-second agg, " + MvRewriteType.SUBSTITUTION.name(),
        result);
    result = routine(sql, mvSql, MvRewriteType.SPJG);
    printInfo("Query 26, aggregate on aggregate, case 3 mv-second agg, " + MvRewriteType.SPJG.name(), result);

    /// Query 13, aggregate on aggregate
    // Case 1. Materialize whole query
    sql = FileUtils.getFileContent("tpch/queries/13.sql");
    result = routine(sql, sql, MvRewriteType.SUBSTITUTION);
    printInfo("Query 13, aggregate on aggregate, case 1 mv-whole, " + MvRewriteType.SUBSTITUTION.name(),
        result);
    result = routine(sql, sql, MvRewriteType.SPJG);
    printInfo("Query 13, aggregate on aggregate, case 1 mv-whole, " + MvRewriteType.SPJG.name(), result);

    // Case 2. Materialize agg on agg
    mvSql = FileUtils.getFileContent("tpch/mvs/13-1.sql");
    result = routine(sql, mvSql, MvRewriteType.SUBSTITUTION);
    printInfo("Query 13, aggregate on aggregate, case 2 mv-agg on agg, " + MvRewriteType.SUBSTITUTION.name(),
        result);
    result = routine(sql, mvSql, MvRewriteType.SPJG);
    printInfo("Query 13, aggregate on aggregate, case 2 mv-agg on agg, " + MvRewriteType.SPJG.name(), result);

    // Case 3. Materialize second agg
    mvSql = FileUtils.getFileContent("tpch/mvs/13-2.sql");
    result = routine(sql, mvSql, MvRewriteType.SUBSTITUTION);
    printInfo("Query 13, aggregate on aggregate, case 3 mv-second agg, " + MvRewriteType.SUBSTITUTION.name(),
        result);
    result = routine(sql, mvSql, MvRewriteType.SPJG);
    printInfo("Query 13, aggregate on aggregate, case 3 mv-second agg, " + MvRewriteType.SPJG.name(), result);
  }
}
