package me.alaneuler.calcite.ng.demo.config;

import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.server.DdlExecutor;

public class SchemaConfig {
  public static void addTable(String schemaName, String tableName, Table table) {
    getSchema(schemaName).add(tableName, table);
  }

  private static SchemaPlus getSchema(String dbName) {
    SchemaPlus rootSchema = GlobalConfig.INSTANCE.getFrameworkConfig().getDefaultSchema();
    if (rootSchema.getName().equals(dbName)) {
      return rootSchema;
    }
    return rootSchema.getSubSchema(dbName);
  }

  public static DdlExecutor ddlExecutor() {
    return GlobalConfig.INSTANCE.getFrameworkConfig().getParserConfig().parserFactory().getDdlExecutor();
  }
}
