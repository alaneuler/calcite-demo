package me.alaneuler.calcite.ng.demo.util;

import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import java.util.List;

public class CommonTableMain {
  static {
    createCommonTable();
  }

  public static void createCommonTable() {
    TableUtils.createTable(
        "",
        "pt_user",
        List.of(
            Pair.of("id", SqlTypeName.INTEGER),
            Pair.of("name", SqlTypeName.VARCHAR),
            Pair.of("age", SqlTypeName.INTEGER)
        )
    );

    TableUtils.createTable(
        "",
        "pt_order",
        List.of(
            Pair.of("id", SqlTypeName.INTEGER),
            Pair.of("user_id", SqlTypeName.INTEGER),
            Pair.of("goods", SqlTypeName.VARCHAR),
            Pair.of("price", SqlTypeName.DECIMAL)
        )
    );
  }
}
