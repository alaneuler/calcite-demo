package me.alaneuler.calcite.ng.demo.materialize.tpch;

import static me.alaneuler.calcite.ng.demo.materialize.tpch.TpchBaseMain.MvRewriteType.SPJG;
import static me.alaneuler.calcite.ng.demo.materialize.tpch.TpchBaseMain.MvRewriteType.SUBSTITUTION;

import me.alaneuler.calcite.ng.demo.util.FileUtils;

public class TpchAggOnAggMain extends TpchBaseMain {
  public static void main(String[] args) {
    String sql, mvSql;
    RoutineResult result;

    /// Query 26, aggregate on aggregate
    // Case 1. Materialize whole query
    sql = FileUtils.getFileContent("tpch/queries/26.sql");
    result = routine(SUBSTITUTION, sql, sql);
    printInfo("Query 26, aggregate on aggregate, case 1 mv-whole, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, sql);
    printInfo("Query 26, aggregate on aggregate, case 1 mv-whole, " + SPJG.name(), result);

    // Case 2. Materialize agg on agg
    mvSql = FileUtils.getFileContent("tpch/mvs/26-1.sql");
    result = routine(SUBSTITUTION, sql, mvSql);
    printInfo(
        "Query 26, aggregate on aggregate, case 2 mv-agg on agg, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, mvSql);
    printInfo("Query 26, aggregate on aggregate, case 2 mv-agg on agg, " + SPJG.name(), result);

    // Case 3. Materialize second agg
    mvSql = FileUtils.getFileContent("tpch/mvs/26-2.sql");
    result = routine(SUBSTITUTION, sql, mvSql);
    printInfo(
        "Query 26, aggregate on aggregate, case 3 mv-second agg, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, mvSql);
    printInfo("Query 26, aggregate on aggregate, case 3 mv-second agg, " + SPJG.name(), result);

    // Case 4. Materialize first and second agg
    result =
        routine(
            SUBSTITUTION,
            sql,
            FileUtils.getFileContent("tpch/mvs/26-2.sql"),
            FileUtils.getFileContent("tpch/mvs/26-3.sql"));
    printInfo(
        "Query 26, aggregate on aggregate, case 4 mv-(first and second agg), "
            + SUBSTITUTION.name(),
        result);
    result =
        routine(
            SPJG,
            sql,
            FileUtils.getFileContent("tpch/mvs/26-2.sql"),
            FileUtils.getFileContent("tpch/mvs/26-3.sql"));
    printInfo(
        "Query 26, aggregate on aggregate, case 4 mv-(first and second agg), " + SPJG.name(),
        result);

    /// Query 13, aggregate on aggregate
    // Case 1. Materialize whole query
    sql = FileUtils.getFileContent("tpch/queries/13.sql");
    result = routine(SUBSTITUTION, sql, sql);
    printInfo("Query 13, aggregate on aggregate, case 1 mv-whole, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, sql);
    printInfo("Query 13, aggregate on aggregate, case 1 mv-whole, " + SPJG.name(), result);

    // Case 2. Materialize agg on agg
    mvSql = FileUtils.getFileContent("tpch/mvs/13-1.sql");
    result = routine(SUBSTITUTION, sql, mvSql);
    printInfo(
        "Query 13, aggregate on aggregate, case 2 mv-agg on agg, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, mvSql);
    printInfo("Query 13, aggregate on aggregate, case 2 mv-agg on agg, " + SPJG.name(), result);

    // Case 3. Materialize second agg
    mvSql = FileUtils.getFileContent("tpch/mvs/13-2.sql");
    result = routine(SUBSTITUTION, sql, mvSql);
    printInfo(
        "Query 13, aggregate on aggregate, case 3 mv-second agg, " + SUBSTITUTION.name(), result);
    result = routine(SPJG, sql, mvSql);
    printInfo("Query 13, aggregate on aggregate, case 3 mv-second agg, " + SPJG.name(), result);
  }
}
