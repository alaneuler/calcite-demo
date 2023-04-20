package me.alaneuler.calcite.ng.demo.materialize.tpch;

import me.alaneuler.calcite.ng.demo.util.FileUtils;
import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.SqlUtils;
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

  protected static RoutineResult routine(MvRewriteType rewriteType,
      String query, String... mvSqls) {
    RelNode rel;
    if (rewriteType.equals(MvRewriteType.SPJG)) {
      rel = RelUtils.sqlToRel(query,
          Map.of("materializationsEnabled", "false"));
    } else {
      rel = RelUtils.sqlToRel(query);
      // No need to convert to Calc, since the SUBSTITUTION will do this for us.
      // rel = toCalc(rel);
    }

    // RelUtils.dump(rel);
    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(rel);
    planner.setTopDownOpt(true);
    planner.setNoneConventionHasInfiniteCost(false);
    if (MvRewriteType.SPJG.equals(rewriteType)) {
      RelOptRules.MATERIALIZATION_RULES.forEach(planner::addRule);
    }
    int i = 0;
    for (String mvSql : mvSqls) {
      RelOptMaterialization materialization = MaterializeUtils
          .createMaterialization("mv" + ++i, mvSql, rel.getCluster(), true);
      planner.addMaterialization(materialization);
    }

    planner.setRoot(rel);
    RelNode after = planner.findBestExp();
    // RelUtils.dump(after);
    return new RoutineResult(rel, after, rewriteType);
  }

  protected static void printInfo(String banner, RoutineResult result) {
    System.out.println("### " + banner + " ###");
    System.out.println("### Before:");
    System.out.print(result.getBefore().explain());
    boolean success = result.getAfter().explain().contains("table=[[mv");
    if (success) {
      System.out.println("### After:");
      System.out.println(SqlUtils.toSqlString(result.getAfter()));
      System.out.print(result.getAfter().explain());
    }

    System.out.println(
        result.getType() + " rewrite " + (success ? "succeed" : "failed"));
    System.out.println();
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
