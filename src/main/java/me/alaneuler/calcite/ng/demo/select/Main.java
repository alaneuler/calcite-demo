package me.alaneuler.calcite.ng.demo.select;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.rel.RelNode;

public class Main extends CommonTableMain {
  public static void main(String[] args) {
    String sql = """
        SELECT * from pt_user as t1 where t1.id = 1
        """;

    RelNode relNode = RelUtils.sqlToRel(sql);
  }
}
