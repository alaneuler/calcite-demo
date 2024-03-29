package me.alaneuler.calcite.ng.demo.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

public class MaterializeUtils {
  public static RelOptMaterialization createMaterialization(
      String mvTableName, String mvSql, RelOptCluster cluster) {
    return createMaterialization(mvTableName, mvSql, cluster, true);
  }

  public static RelOptMaterialization createMaterialization(
      String mvTableName, String mvSql, RelOptCluster cluster, boolean createMvTable) {
    RelNode mvRel = RelUtils.sqlToRel(mvSql);
    RelUtils.setCluster(mvRel, cluster);
    if (createMvTable) {
      createTableForMvRel(mvRel, mvTableName);
    }

    LogicalTableScan tableScan = RelUtils.createTableScan(mvTableName);
    RelUtils.setCluster(tableScan, cluster);

    return new RelOptMaterialization(
        tableScan, mvRel, null, Lists.newArrayList(mvTableName.split("\\.")));
  }

  private static void createTableForMvRel(RelNode mvRel, String tableName) {
    List<Pair<String, SqlTypeName>> fields = new ArrayList<>();
    for (String fieldName : mvRel.getRowType().getFieldNames()) {
      fields.add(
          Pair.of(
              fieldName,
              mvRel.getRowType().getField(fieldName, true, false).getType().getSqlTypeName()));
    }
    TableUtils.createTable("", tableName, fields);
  }
}
