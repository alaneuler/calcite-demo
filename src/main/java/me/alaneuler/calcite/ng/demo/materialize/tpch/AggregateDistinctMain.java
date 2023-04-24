package me.alaneuler.calcite.ng.demo.materialize.tpch;

import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.rel.RelNode;

public class AggregateDistinctMain extends TpchBaseMain {
  public static void main(String[] args) {
    String sql = """
        SELECT p_brand,
               p_type,
               p_size,
               count(DISTINCT ps_suppkey) AS supplier_cnt
        FROM tpch_partsupp,
             tpch_part
        WHERE p_partkey = ps_partkey
        GROUP BY p_brand,
                 p_type,
                 p_size
        """;
    RelNode rel = RelUtils.sqlToRel(sql);
    System.out.println(rel.explain());

    sql = """
        SELECT p_brand,
               p_type,
               p_size,
               COUNT(ps_suppkey) AS supplier_cnt
        FROM (
            SELECT DISTINCT
                   p_brand,
                   p_type,
                   p_size,
                   ps_suppkey
            FROM tpch_partsupp,
                 tpch_part
            WHERE p_partkey = ps_partkey
        ) AS unique_suppliers
        GROUP BY p_brand,
                 p_type,
                 p_size
        """;
    rel = RelUtils.sqlToRel(sql);
    System.out.println(rel.explain());
  }
}
