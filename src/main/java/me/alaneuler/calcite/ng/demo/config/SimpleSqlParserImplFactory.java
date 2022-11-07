package me.alaneuler.calcite.ng.demo.config;

import org.apache.calcite.server.DdlExecutor;
import org.apache.calcite.server.ServerDdlExecutor;
import org.apache.calcite.sql.parser.SqlAbstractParserImpl;
import org.apache.calcite.sql.parser.SqlParserImplFactory;
import org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl;
import org.apache.calcite.util.SourceStringReader;

import java.io.Reader;

public class SimpleSqlParserImplFactory implements SqlParserImplFactory {
  @Override
  public SqlAbstractParserImpl getParser(Reader reader) {
    final SqlDdlParserImpl parser = new SqlDdlParserImpl(reader);
    if (reader instanceof SourceStringReader) {
      final String sql =
          ((SourceStringReader) reader).getSourceString();
      parser.setOriginalSql(sql);
    }
    return parser;
  }

  @Override
  public DdlExecutor getDdlExecutor() {
    return ServerDdlExecutor.INSTANCE;
  }
}
