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

import java.util.Map;

public class TpchBaseMain {
  enum MvRewriteType {
    SPJG, SUBSTITUTION,
  }

  protected static RoutineResult routine(String sql, String mvSql,
      MvRewriteType rewriteType) {
    RelNode rel;
    if (rewriteType.equals(MvRewriteType.SPJG)) {
      rel = RelUtils.sqlToRel(sql, Map.of("materializationsEnabled", "false"));
    } else {
      rel = RelUtils.sqlToRel(sql);
    }

    // RelUtils.dump(rel);

    RelOptMaterialization materialization = MaterializeUtils
        .createMaterialization("mv", mvSql, rel.getCluster(), true);
    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(rel);
    planner.setTopDownOpt(true);
    planner.setNoneConventionHasInfiniteCost(false);
    if (MvRewriteType.SPJG.equals(rewriteType)) {
      RelOptRules.MATERIALIZATION_RULES.forEach(planner::addRule);
    }
    planner.addMaterialization(materialization);

    planner.setRoot(rel);
    RelNode after = planner.findBestExp();
    // RelUtils.dump(after);
    return new RoutineResult(rel, after, materialization.queryRel, rewriteType);
  }

  static {
    createTpchTables();
  }

  private static void createTpchTables() {
    FileUtils.getFilesContents("tpch/tables").forEach(TableUtils::createTable);
  }
}
