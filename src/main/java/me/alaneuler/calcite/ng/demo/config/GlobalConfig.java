package me.alaneuler.calcite.ng.demo.config;

import lombok.Getter;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.server.CalciteServerStatement;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.fun.SqlLibrary;
import org.apache.calcite.sql.fun.SqlLibraryOperatorTableFactory;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.util.SqlOperatorTables;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Set;

public class GlobalConfig {
  @Getter
  private FrameworkConfig frameworkConfig;

  @Getter
  private CalcitePrepare.Context px;

  @Getter
  private SqlParser.Config sqlParserConfig;

  public static final GlobalConfig INSTANCE = new GlobalConfig();

  private GlobalConfig() {
    this.px = prepareContext();
    this.sqlParserConfig = parserConfig();

    this.frameworkConfig = Frameworks.newConfigBuilder().parserConfig(this.sqlParserConfig)
        .defaultSchema(this.px.getMutableRootSchema().plus()).context(new ConfigContext(Map.of()))
        .operatorTable(operatorTable()).build();
  }

  /**
   * Connect to the Calcite and get the connection.
   */
  private CalcitePrepare.Context prepareContext() {
    try {
      Connection connection = DriverManager.getConnection("jdbc:calcite:");
      return connection.createStatement().unwrap(CalciteServerStatement.class)
          .createPrepareContext();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private SqlParser.Config parserConfig() {
    return SqlParser.config().withConformance(SqlConformanceEnum.MYSQL_5)
        .withQuoting(Quoting.BACK_TICK).withCaseSensitive(false)
        .withParserFactory(new SimpleSqlParserImplFactory()).withUnquotedCasing(Casing.UNCHANGED);
  }

  public static SqlOperatorTable operatorTable() {
    return SqlOperatorTables.chain(SqlStdOperatorTable.instance(),
        SqlLibraryOperatorTableFactory.INSTANCE
            .getOperatorTable(Set.of(SqlLibrary.MYSQL, SqlLibrary.POSTGRESQL, SqlLibrary.ORACLE)));
  }
}
