package me.alaneuler.calcite.ng.demo.rules;

import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.hep.HepProgram;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.rules.MaterializedViewFilterScanRule;
import org.apache.calcite.rel.rules.materialize.MaterializedViewOnlyAggregateRule;
import org.apache.calcite.rel.rules.materialize.MaterializedViewOnlyFilterRule;
import org.apache.calcite.rel.rules.materialize.MaterializedViewOnlyJoinRule;
import org.apache.calcite.rel.rules.materialize.MaterializedViewProjectAggregateRule;
import org.apache.calcite.rel.rules.materialize.MaterializedViewProjectFilterRule;
import org.apache.calcite.rel.rules.materialize.MaterializedViewProjectJoinRule;

public class OptimizationRelatedRules {
  private static final HepProgram PROGRAM = new HepProgramBuilder()
      .addRuleInstance(CoreRules.FILTER_PROJECT_TRANSPOSE)
      .addRuleInstance(CoreRules.JOIN_PROJECT_BOTH_TRANSPOSE)
      .addRuleInstance(CoreRules.JOIN_PROJECT_LEFT_TRANSPOSE)
      .addRuleInstance(CoreRules.JOIN_PROJECT_RIGHT_TRANSPOSE)
      .addRuleInstance(CoreRules.PROJECT_MERGE)
      .addRuleInstance(CoreRules.FILTER_MERGE)
      .addRuleInstance(CoreRules.PROJECT_REMOVE).build();

  public static final MaterializedViewProjectFilterRule INSTANCE_PROJECT_FILTER = (MaterializedViewProjectFilterRule) MaterializedViewProjectFilterRule.Config.DEFAULT
      .withGenerateUnionRewriting(true).withFastBailOut(false)
      .withUnionRewritingPullProgram(PROGRAM)
      .withRelBuilderFactory(RelFactories.LOGICAL_BUILDER).toRule();

  public static final MaterializedViewOnlyFilterRule INSTANCE_FILTER = (MaterializedViewOnlyFilterRule) MaterializedViewOnlyFilterRule.Config.DEFAULT
      .withGenerateUnionRewriting(true).withFastBailOut(false)
      .withUnionRewritingPullProgram(PROGRAM)
      .withRelBuilderFactory(RelFactories.LOGICAL_BUILDER).toRule();
  public static final MaterializedViewProjectJoinRule INSTANCE_PROJECT_JOIN = (MaterializedViewProjectJoinRule) MaterializedViewProjectJoinRule.Config.DEFAULT
      .withGenerateUnionRewriting(true).withFastBailOut(false)
      .withUnionRewritingPullProgram(PROGRAM)
      .withRelBuilderFactory(RelFactories.LOGICAL_BUILDER).toRule();

  public static final MaterializedViewOnlyJoinRule INSTANCE_JOIN = (MaterializedViewOnlyJoinRule) MaterializedViewOnlyJoinRule.Config.DEFAULT
      .withGenerateUnionRewriting(true).withFastBailOut(false)
      .withUnionRewritingPullProgram(PROGRAM)
      .withRelBuilderFactory(RelFactories.LOGICAL_BUILDER).toRule();

  public static final MaterializedViewOnlyAggregateRule INSTANCE_AGGREGATE = (MaterializedViewOnlyAggregateRule) MaterializedViewOnlyAggregateRule.Config.DEFAULT
      .withFastBailOut(false).toRule();

  public static final MaterializedViewProjectAggregateRule INSTANCE_PROJECT_AGGREGATE = (MaterializedViewProjectAggregateRule) MaterializedViewProjectAggregateRule.Config.DEFAULT
      .withFastBailOut(false).toRule();

  public static final MaterializedViewFilterScanRule INSTANCE_FILTER_SCAN = MaterializedViewFilterScanRule.Config.DEFAULT
      .toRule();

  public static final RelOptRule[] MATERIALIZED_VIEW_REWRITING_RULES = new RelOptRule[]{
      INSTANCE_PROJECT_FILTER, INSTANCE_FILTER, INSTANCE_PROJECT_JOIN,
      INSTANCE_JOIN, INSTANCE_PROJECT_AGGREGATE, INSTANCE_AGGREGATE,
      INSTANCE_FILTER_SCAN};

  public static final RelOptRule[] PRE_MV_REWRITING_RULES_FOR_UNION_PULL_TO_TOP = new RelOptRule[]{
      // Add the following two rules to ensure successfully mv rewrite when face
      // realTime table
      // and history Table union.
      CoreRules.JOIN_LEFT_UNION_TRANSPOSE, CoreRules.JOIN_RIGHT_UNION_TRANSPOSE,
      // Add this rule to ensure the projection operator is underneath the union
      // operator.
      CoreRules.PROJECT_SET_OP_TRANSPOSE,
      // Add this rule also to ensure successfully mv rewrite when face realTime
      // table
      // and
      // history Table union.
      // CoreRules.AGGREGATE_UNION_TRANSPOSE,
      CoreRules.SORT_UNION_TRANSPOSE, CoreRules.UNION_MERGE,
      CoreRules.UNION_REMOVE,};

  public static final RelOptRule[] MV_REWRITING_RULES_FOR_MATCH_MORE_OPERAND = new RelOptRule[]{
      // Project need on filter/aggregate/join.
      CoreRules.FILTER_PROJECT_TRANSPOSE,
      CoreRules.JOIN_PROJECT_RIGHT_TRANSPOSE,
      CoreRules.JOIN_PROJECT_LEFT_TRANSPOSE, CoreRules.PROJECT_MERGE,
      CoreRules.FILTER_MERGE, CoreRules.AGGREGATE_MERGE,
      // Filter need to push into aggregate/join to support partly rewrite.
      CoreRules.FILTER_AGGREGATE_TRANSPOSE, CoreRules.FILTER_INTO_JOIN};

  // Compare with PRE_MV_REWRITING_RULES_FOR_UNION_PULL_TO_TOP , do not use
  // AGGREGATE_UNION_TRANSPOSE rule to make logical plan lower.
  public static final RelOptRule[] POST_MV_REWRITING_RULES_FOR_UNION_PULL_TO_TOP = new RelOptRule[]{
      // Add the following two rules to ensure successfully mv rewrite when face
      // realTime table
      // and history Table union.
      CoreRules.JOIN_LEFT_UNION_TRANSPOSE, CoreRules.JOIN_RIGHT_UNION_TRANSPOSE,
      // Add this rule to ensure the projection operator is underneath the union
      // operator.
      CoreRules.PROJECT_SET_OP_TRANSPOSE,
      // Add this rule also to ensure successfully mv rewrite when face realTime
      // table
      // and
      // history Table union.
      CoreRules.UNION_MERGE, CoreRules.UNION_REMOVE,};

  public static final RelOptRule[] POST_MV_REWRITE_RULES_FOR_PUSH_PROJECT = new RelOptRule[]{
      // Push project.
      CoreRules.PROJECT_SET_OP_TRANSPOSE, CoreRules.PROJECT_FILTER_TRANSPOSE,
      CoreRules.PROJECT_JOIN_TRANSPOSE, CoreRules.PROJECT_MERGE,
      CoreRules.FILTER_MERGE,
      // Because of FILTER_REDUCE_EXPRESSIONS rule only support LogicalFilter,so
      // add this rule before cbo optimization when enableCBO value is true.
      CoreRules.FILTER_REDUCE_EXPRESSIONS, CoreRules.AGGREGATE_MERGE,
      // JOIN_EXPAND_IS_NOT_DISTINCT_FROM
      // CoreRules.AGGREGATE_REMOVE,
  };

  public static final RelOptRule[] POST_MV_REWRITE_RULES_FOR_PLAN_MORE_SIMPLER = new RelOptRule[]{
      /*
       * TODO(tongsuo): This rule will push windows function in select statement
       * to where statement which CK doesn't support, remove it for temporary.
       */
      // CoreRules.FILTER_TO_CALC,
      CoreRules.PROJECT_TO_CALC, CoreRules.FILTER_CALC_MERGE,
      CoreRules.PROJECT_CALC_MERGE, CoreRules.CALC_MERGE, CoreRules.CALC_REMOVE,
      CoreRules.CALC_REDUCE_EXPRESSIONS, CoreRules.CALC_REDUCE_DECIMALS,};
}
