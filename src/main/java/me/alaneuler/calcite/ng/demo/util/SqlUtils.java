package me.alaneuler.calcite.ng.demo.util;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.dialect.ClickHouseSqlDialect;

public class SqlUtils {
  private static final SqlDialect DEFAULT_DIALECT = ClickHouseSqlDialect.DEFAULT;

  public static String toSqlString(RelNode root, SqlDialect dialect) {
    RelToSqlConverter converter = new RelToSqlConverter(dialect);
    return converter.visitRoot(root).asStatement().toSqlString(dialect).getSql();
  }

  public static String toSqlString(RelNode root) {
    return toSqlString(root, DEFAULT_DIALECT);
  }
}
