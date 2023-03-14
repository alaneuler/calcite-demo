package me.alaneuler.calcite.ng.demo.util;

import lombok.Getter;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.util.Pair;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GraphvizWriter implements RelWriter {
  private final Map<String, Object> attrs = new LinkedHashMap<>();

  @Getter
  private String result;

  @Override
  public void explain(RelNode rel, List<Pair<String, @Nullable Object>> valueList) {
    throw new UnsupportedOperationException();
  }

  @Override
  public SqlExplainLevel getDetailLevel() {
    return null;
  }

  @Override
  public RelWriter input(String term, RelNode input) {
    // Ignore all input, because we're outputting node in a graph.
    return this;
  }

  @Override
  public RelWriter item(String term, Object value) {
    attrs.put(term, value);
    return this;
  }

  @Override
  public RelWriter done(RelNode node) {
    StringBuilder sb = new StringBuilder();
    sb.append(node.getRelTypeName());
    sb.append("-").append(node.getId());
    sb.append("\\n");
    sb.append('(');
    int j = 0;
    for (Map.Entry<String, Object> entry: attrs.entrySet()) {
      if (j++ > 0) {
        sb.append(",");
      }

      sb.append(entry.getKey());
      sb.append('=');
      sb.append(entry.getValue());
    }
    sb.append(')');
    sb.append("\\n");
    sb.append(node.getTraitSet());
    result = sb.toString();
    return this;
  }
}
