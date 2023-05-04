package me.alaneuler.calcite.ng.demo.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;

public class VolcanoUtils {
  public static void dump(VolcanoPlanner planner) {
    System.out.println(dumpStr(planner));
  }

  public static String dumpStr(VolcanoPlanner planner) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    planner.dump(pw);
    return sw.toString();
  }

  public static VolcanoPlanner extractVolcanoPlanner(RelNode relNode) {
    VolcanoPlanner planner = (VolcanoPlanner) relNode.getCluster().getPlanner();
    planner.clear();
    return planner;
  }
}
