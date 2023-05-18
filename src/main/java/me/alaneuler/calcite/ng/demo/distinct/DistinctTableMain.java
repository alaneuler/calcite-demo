package me.alaneuler.calcite.ng.demo.distinct;

import java.util.List;
import me.alaneuler.calcite.ng.demo.util.TableUtils;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

public class DistinctTableMain {
  static {
    createTable();
  }

  private static void createTable() {
    TableUtils.createTable(
        "",
        "pt_user",
        List.of(
            Pair.of("id", SqlTypeName.INTEGER),
            Pair.of("first_name", SqlTypeName.VARCHAR),
            Pair.of("last_name", SqlTypeName.VARCHAR),
            Pair.of("age", SqlTypeName.INTEGER),
            Pair.of("state", SqlTypeName.VARCHAR),
            Pair.of("country", SqlTypeName.VARCHAR),
            Pair.of("signupDate", SqlTypeName.INTEGER)));
  }
}
