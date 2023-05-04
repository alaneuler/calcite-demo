package me.alaneuler.calcite.ng.demo.util;

import java.util.List;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

public class CommonTableMain {
  static {
    createCommonTable();
  }

  private static void createCommonTable() {
    TableUtils.createTable(
        "",
        "pt_user",
        List.of(
            Pair.of("id", SqlTypeName.INTEGER),
            Pair.of("name", SqlTypeName.VARCHAR),
            Pair.of("age", SqlTypeName.INTEGER),
            Pair.of("address", SqlTypeName.VARCHAR)));

    TableUtils.createTable(
        "",
        "pt_order",
        List.of(
            Pair.of("id", SqlTypeName.INTEGER),
            Pair.of("user_id", SqlTypeName.INTEGER),
            Pair.of("goods", SqlTypeName.VARCHAR),
            Pair.of("price", SqlTypeName.DECIMAL)));
  }
}
