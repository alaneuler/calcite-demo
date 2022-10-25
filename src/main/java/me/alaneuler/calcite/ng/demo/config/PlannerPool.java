package me.alaneuler.calcite.ng.demo.config;

import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

public class PlannerPool {
  public static Planner getPlanner() {
    FrameworkConfig config = GlobalConfig.INSTANCE.getFrameworkConfig();
    return Frameworks.getPlanner(config);
  }
}
