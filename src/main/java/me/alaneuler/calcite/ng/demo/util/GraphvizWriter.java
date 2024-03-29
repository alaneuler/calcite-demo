package me.alaneuler.calcite.ng.demo.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.util.Pair;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A {@link RelWriter} for generating Graphviz dot file. This writer ignores {@link RelNode}'s
 * inputs for better displaying.
 */
public class GraphvizWriter implements RelWriter {
  /** Use LinkedHashMap here to have items ordered. */
  private final Map<String, Object> attrs = new LinkedHashMap<>();

  @Getter private String result;

  private boolean displayType;

  private boolean displayTrait;

  public GraphvizWriter(boolean displayType, boolean displayTrait) {
    this.displayType = displayType;
    this.displayTrait = displayTrait;
  }

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
    if (displayType) {
      sb.append(node.getRelTypeName());
      sb.append("-").append(node.getId());
      sb.append("\\n");
    }
    sb.append('(');
    int j = 0;
    int lineCharCount = 0;
    for (Map.Entry<String, Object> entry : attrs.entrySet()) {
      if (j++ > 0) {
        sb.append(",");
      }
      // For better graph visualization display: start a new line if current
      // line exceed 20
      // characters.
      if (lineCharCount > 20) {
        lineCharCount = 0;
        sb.append("\\n");
      }

      sb.append(entry.getKey());
      sb.append('=');
      sb.append(entry.getValue());
      lineCharCount += entry.getKey().length() + entry.getValue().toString().length();
    }
    sb.append(')');

    if (displayTrait) {
      sb.append("\\n");
      sb.append(node.getTraitSet());
    }
    result = sb.toString();
    return this;
  }
}
