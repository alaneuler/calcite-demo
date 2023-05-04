package me.alaneuler.calcite.ng.demo.util;

import java.util.List;
import me.alaneuler.calcite.ng.demo.config.SchemaConfig;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

public class TableUtils {
  /** Create a table via fields definition. */
  public static void createTable(
      String schemaName, String tableName, List<Pair<String, SqlTypeName>> fields) {
    SimpleTable table = new SimpleTable();
    fields.forEach(field -> table.addField(field.getKey(), field.getValue()));
    SchemaConfig.addTable(schemaName, tableName, table);
  }

  public static void createTable(String sql) {
    SchemaConfig.addTable(sql);
  }
}
