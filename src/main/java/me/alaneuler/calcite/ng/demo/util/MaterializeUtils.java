package me.alaneuler.calcite.ng.demo.util;

import com.google.common.collect.Lists;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalTableScan;

public class MaterializeUtils {
  public static RelOptMaterialization createMaterialization(String mvTableName, String mvSql, RelOptCluster cluster) {
    LogicalTableScan tableScan = RelUtils.createTableScan(mvTableName);
    RelUtils.setCluster(tableScan, cluster);
    RelNode mvRel = RelUtils.sqlToRel(mvSql);
    return new RelOptMaterialization(tableScan,
        mvRel, null, Lists.newArrayList(mvTableName.split("\\.")));
  }
}
