package me.alaneuler.calcite.ng.demo.materialize;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.TableUtils;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import java.util.List;

public class MaterializeBaseMain extends CommonTableMain {
  static {
    createTables();
  }

  private static void createTables() {
    TableUtils.createTable(
        "",
        "tbl",
        List.of(
            Pair.of("id", SqlTypeName.INTEGER),
            Pair.of("col1", SqlTypeName.VARCHAR),
            Pair.of("ts", SqlTypeName.TIMESTAMP)
        )
    );

    TableUtils.createTable(
        "",
        "tbl_mv",
        List.of(
            Pair.of("col1", SqlTypeName.VARCHAR),
            Pair.of("dt", SqlTypeName.DATE),
            Pair.of("cnt", SqlTypeName.INTEGER)
        )
    );
  }
}
