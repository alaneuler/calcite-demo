package me.alaneuler.calcite.ng.demo.config;

import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.server.DdlExecutor;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;

public class SchemaConfig {
  public static void addTable(String schemaName, String tableName, Table table) {
    getSchema(schemaName).add(tableName, table);
  }

  public static void addTable(String sql) {
    executeDdl(sql);
  }

  private static void executeDdl(String ddl) {
    try {
      SqlParser parser = SqlParser.create(ddl, GlobalConfig.INSTANCE.getSqlParserConfig());
      SqlNode node = parser.parseStmt();
      ddlExecutor().executeDdl(GlobalConfig.INSTANCE.getPx(), node);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
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
