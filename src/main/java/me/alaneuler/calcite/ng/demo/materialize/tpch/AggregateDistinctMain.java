package me.alaneuler.calcite.ng.demo.materialize.tpch;

import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;

public class AggregateDistinctMain extends TpchBaseMain {
  public static void main(String[] args) {
    String sql =
        """
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
    String mvTableName = "tbl_mv";
    String mvSql =
        """
        SELECT DISTINCT
               p_brand,
               p_type,
               p_size,
               ps_suppkey
        FROM tpch_partsupp,
             tpch_part
        WHERE p_partkey = ps_partkey
        """;

    RelNode relNode = RelUtils.sqlToRel(sql);
    RelOptMaterialization materialization =
        MaterializeUtils.createMaterialization(mvTableName, mvSql, relNode.getCluster(), true);

    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(relNode);
    planner.setTopDownOpt(true);
    planner.setNoneConventionHasInfiniteCost(false);
    planner.addMaterialization(materialization);

    planner.setRoot(relNode);
    RelNode after = planner.findBestExp();
    RelUtils.dump(after);
  }
}
