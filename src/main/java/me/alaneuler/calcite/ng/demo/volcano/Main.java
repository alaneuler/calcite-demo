package me.alaneuler.calcite.ng.demo.volcano;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlExplainLevel;

public class Main extends CommonTableMain {
  public static void main(String[] args) {
    String sql = """
        SELECT * FROM pt_user
        """;

    RelNode rel = RelUtils.sqlToRel(sql);
    System.out.println(RelOptUtil.toString(rel, SqlExplainLevel.ALL_ATTRIBUTES));

    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(rel);
    RelOptCluster cluster = rel.getCluster();

    RelTraitSet traits = cluster.traitSet().replace(EnumerableConvention.INSTANCE);
    rel = planner.changeTraits(rel, traits);
    System.out.println(RelOptUtil.toString(rel, SqlExplainLevel.ALL_ATTRIBUTES));

    planner.setRoot(rel);
    planner.findBestExp();
    VolcanoUtils.dump(planner);
  }
}
