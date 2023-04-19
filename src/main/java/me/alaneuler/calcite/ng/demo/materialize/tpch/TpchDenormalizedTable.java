package me.alaneuler.calcite.ng.demo.materialize.tpch;

import me.alaneuler.calcite.ng.demo.util.FileUtils;

/**
 * 大宽表场景
 */
public class TpchDenormalizedTable extends TpchBaseMain {
  public static void main(String[] args) {
    // Query 5, inner join
    String sql = FileUtils.getFileContent("tpch/queries/5.sql");
    String mvSql = FileUtils.getFileContent("tpch/mv/5-1.sql");
    route(sql, mvSql);

    mvSql = FileUtils.getFileContent("tpch/mv/5-2.sql");
    route(sql, mvSql);

    // Query 25, left join
    sql = FileUtils.getFileContent("tpch/queries/25.sql");
    mvSql = FileUtils.getFileContent("tpch/mv/25-1.sql");
    route(sql, mvSql);
  }
}
