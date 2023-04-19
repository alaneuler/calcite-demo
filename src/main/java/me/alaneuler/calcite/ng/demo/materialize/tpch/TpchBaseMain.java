package me.alaneuler.calcite.ng.demo.materialize.tpch;

import me.alaneuler.calcite.ng.demo.util.FileUtils;
import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.TableUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.RelOptRules;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;

public class TpchBaseMain {
  protected static void route(String sql, String mvSql) {
    RelNode rel = RelUtils.sqlToRel(sql);
    RelUtils.dump(rel);

    RelOptMaterialization materialization = MaterializeUtils
        .createMaterialization("mv", mvSql, rel.getCluster(), true);
    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(rel);
    planner.setTopDownOpt(true);
    planner.setNoneConventionHasInfiniteCost(false);
    RelOptRules.MATERIALIZATION_RULES.forEach(planner::addRule);
    planner.addMaterialization(materialization);

    planner.setRoot(rel);
    RelNode after = planner.findBestExp();
    RelUtils.dump(after);
  }

  static {
    createTpchTables();
  }

  private static void createTpchTables() {
    FileUtils.getFilesContents("tpch/tables").forEach(TableUtils::createTable);
  }
}
