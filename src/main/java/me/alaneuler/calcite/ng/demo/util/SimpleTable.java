package me.alaneuler.calcite.ng.demo.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.rel.type.StructKind;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;

public class SimpleTable extends AbstractTable implements ScannableTable {
  private List<String> fieldNames = new ArrayList<>();
  private List<SqlTypeName> fieldTypes = new ArrayList<>();

  private RelDataType rowType;

  @Override
  public RelDataType getRowType(RelDataTypeFactory typeFactory) {
    if (rowType == null) {
      List<RelDataTypeField> fields = new ArrayList<>(fieldNames.size());

      for (int i = 0; i < fieldNames.size(); i++) {
        RelDataType fieldType = typeFactory.createSqlType(fieldTypes.get(i));
        RelDataTypeField field = new RelDataTypeFieldImpl(fieldNames.get(i), i, fieldType);
        fields.add(field);
      }

      rowType = new RelRecordType(StructKind.PEEK_FIELDS, fields, false);
    }

    return rowType;
  }

  public void addField(String name, SqlTypeName typeName) {
    fieldNames.add(name);
    fieldTypes.add(typeName);
  }

  @Override
  public Enumerable<Object[]> scan(DataContext root) {
    return null;
  }
}
