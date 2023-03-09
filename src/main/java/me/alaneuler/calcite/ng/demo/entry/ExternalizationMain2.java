package me.alaneuler.calcite.ng.demo.entry;

import me.alaneuler.calcite.ng.demo.config.GlobalConfig;
import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import me.alaneuler.calcite.ng.demo.rules.OptimizationRelatedRules;
import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.externalize.RelJsonReader;
import org.apache.calcite.rel.externalize.RelJsonWriter;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.Planner;

import java.util.Arrays;

public class ExternalizationMain2 extends CommonTableMain {
  public static void main(String[] args) throws Exception {
    String sql = """
        select name from pt_user where name = 'alaneuler'
        """.trim();

    Planner planner = PlannerPool.getPlanner();
    SqlNode sqlNode = planner.parse(sql);
    sqlNode = planner.validate(sqlNode);
    RelNode relNode = planner.rel(sqlNode).project();
    relNode = rboOptimization(relNode);

    RelJsonWriter writer = new RelJsonWriter();
    relNode.explain(writer);
    String json = writer.asString();

    CalciteCatalogReader catalogReader = new CalciteCatalogReader(
        GlobalConfig.INSTANCE.getPx().getRootSchema(),
        GlobalConfig.INSTANCE.getPx().getDefaultSchemaPath(),
        GlobalConfig.INSTANCE.getPx().getTypeFactory(),
        GlobalConfig.INSTANCE.getPx().config());
    RelJsonReader reader = new RelJsonReader(relNode.getCluster(),
        catalogReader,
        GlobalConfig.INSTANCE.getPx().getRootSchema().plus());
    RelNode relNode1 = reader.read(json);
  }

  public static RelNode rboOptimization(RelNode relNode) {
    HepProgramBuilder preBuilder = new HepProgramBuilder();

    // These rules are used to pull union to the top of logicalPlan.
    preBuilder.addGroupBegin();
    preBuilder.addRuleCollection(
        Arrays.stream(OptimizationRelatedRules.PRE_MV_REWRITING_RULES_FOR_UNION_PULL_TO_TOP)
            .toList());
    preBuilder.addGroupEnd();

    // These rules are used to make mv related rules can match more relNode.
    preBuilder.addGroupBegin();
    preBuilder.addRuleCollection(
        Arrays.stream(OptimizationRelatedRules.MV_REWRITING_RULES_FOR_MATCH_MORE_OPERAND).toList());
    preBuilder.addGroupEnd();

    preBuilder.addGroupBegin();
    preBuilder.addRuleCollection(
        Arrays.stream(OptimizationRelatedRules.POST_MV_REWRITING_RULES_FOR_UNION_PULL_TO_TOP)
            .toList());
    preBuilder.addGroupEnd();

    preBuilder.addGroupBegin();
    preBuilder.addRuleCollection(
        Arrays.stream(OptimizationRelatedRules.POST_MV_REWRITE_RULES_FOR_PUSH_PROJECT).toList());
    preBuilder.addGroupEnd();

    // These rules are used to convert logicalRelNode to CalcNode to make sql more easy.
    preBuilder.addGroupBegin();
    preBuilder.addRuleCollection(
        Arrays.stream(OptimizationRelatedRules.POST_MV_REWRITE_RULES_FOR_PLAN_MORE_SIMPLER)
            .toList());
    preBuilder.addGroupEnd();

    HepPlanner preHepPlanner = new HepPlanner(preBuilder.build());
    preHepPlanner.setRoot(relNode);
    return preHepPlanner.findBestExp();
  }
}
