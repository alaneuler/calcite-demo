package me.alaneuler.calcite.ng.demo.util;

import me.alaneuler.calcite.ng.demo.config.SchemaConfig;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import java.util.List;

public class TableUtils {
  public static void createTable(String schemaName, String tableName, List<Pair<String, SqlTypeName>> fields) {
    SimpleTable table = new SimpleTable();
    fields.forEach(field -> table.addField(field.getKey(), field.getValue()));
    SchemaConfig.addTable(schemaName, tableName, table);
  }
}
