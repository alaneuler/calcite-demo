package me.alaneuler.calcite.ng.demo.config;

import java.util.Map;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

public class PlannerPool {
  public static Planner getPlanner() {
    FrameworkConfig config = GlobalConfig.INSTANCE.getFrameworkConfig();
    return Frameworks.getPlanner(config);
  }

  public static Planner getPlanner(Map<String, String> configs) {
    FrameworkConfig frameworkConfig =
        Frameworks.newConfigBuilder()
            .parserConfig(GlobalConfig.INSTANCE.getSqlParserConfig())
            .defaultSchema(GlobalConfig.INSTANCE.getPx().getMutableRootSchema().plus())
            .context(new ConfigContext(configs))
            .operatorTable(GlobalConfig.operatorTable())
            .build();
    return Frameworks.getPlanner(frameworkConfig);
  }
}
