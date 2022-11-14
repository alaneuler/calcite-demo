package me.alaneuler.calcite.ng.demo.util;

import org.apache.calcite.plan.volcano.VolcanoPlanner;

import java.io.PrintWriter;
import java.io.StringWriter;

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
}
