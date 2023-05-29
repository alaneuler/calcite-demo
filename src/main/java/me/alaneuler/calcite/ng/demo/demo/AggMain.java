package me.alaneuler.calcite.ng.demo.demo;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelDisplayUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.rel.RelNode;

public class AggMain extends CommonTableMain {
  public static void main(String[] args) {
    String sql =
        """
        SELECT
          user_id,
          SUM(price),
          COUNT(*)
        FROM
          pt_user,
          pt_order
        WHERE
          pt_user.id = user_id
          AND age < 18
        GROUP BY
          user_id
            """;
    RelNode rel = RelUtils.sqlToRel(sql);
    RelDisplayUtils.dump(rel);
  }
}
