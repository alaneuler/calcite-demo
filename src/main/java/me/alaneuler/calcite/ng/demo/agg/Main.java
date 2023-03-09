package me.alaneuler.calcite.ng.demo.agg;

import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import org.apache.calcite.tools.Planner;

public class Main extends CommonTableMain {
  public static void main(String[] args) throws Exception {
    String sql = """
        select age, count(*) from pt_user group by age
        """;

    Planner planner = PlannerPool.getPlanner();
    planner.parse(sql);

  }
}
