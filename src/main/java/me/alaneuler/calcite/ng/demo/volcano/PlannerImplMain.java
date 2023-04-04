package me.alaneuler.calcite.ng.demo.volcano;

import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.PlannerImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.Planner;

public class PlannerImplMain extends CommonTableMain {
  public static void main(String[] args) throws Exception {
    String sql = """
        SELECT * FROM pt_user WHERE id = 1
        """;

//    RelNode rel = RelUtils.sqlToRel(sql);
//    System.out.println(RelOptUtil.toString(rel, SqlExplainLevel.ALL_ATTRIBUTES));
    PlannerImpl plannerImpl = (PlannerImpl) PlannerPool.getPlanner();
    SqlNode sqlNode = plannerImpl.parse(sql);
    sqlNode = plannerImpl.validate(sqlNode);
    RelNode rel = plannerImpl.rel(sqlNode).project();

    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(rel);
    RelOptCluster cluster = rel.getCluster();

    RelTraitSet traits = cluster.traitSet().replace(EnumerableConvention.INSTANCE);

    rel = plannerImpl.transform(0, traits, rel);


  }
}
