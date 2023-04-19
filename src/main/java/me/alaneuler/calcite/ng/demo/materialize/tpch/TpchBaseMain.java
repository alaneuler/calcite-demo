package me.alaneuler.calcite.ng.demo.materialize.tpch;

import me.alaneuler.calcite.ng.demo.util.FileUtils;
import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.TableUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.RelOptRules;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;

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
      rel = toCalc(RelUtils.sqlToRel(sql));
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

  private static RelNode toCalc(RelNode rel) {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addGroupBegin();
    builder.addRuleInstance(CoreRules.FILTER_TO_CALC);
    builder.addRuleInstance(CoreRules.PROJECT_TO_CALC);
    builder.addRuleInstance(CoreRules.FILTER_CALC_MERGE);
    builder.addRuleInstance(CoreRules.PROJECT_CALC_MERGE);
    builder.addRuleInstance(CoreRules.CALC_MERGE);
    builder.addRuleInstance(CoreRules.CALC_REDUCE_EXPRESSIONS);
    builder.addRuleInstance(CoreRules.CALC_REDUCE_EXPRESSIONS);
    builder.addRuleInstance(CoreRules.CALC_REMOVE);
    builder.addGroupEnd();
    HepPlanner hepPlanner = new HepPlanner(builder.build());

    hepPlanner.setRoot(rel);
    return hepPlanner.findBestExp();
  }

  static {
    createTpchTables();
  }

  private static void createTpchTables() {
    FileUtils.getFilesContents("tpch/tables").forEach(TableUtils::createTable);
  }
}
