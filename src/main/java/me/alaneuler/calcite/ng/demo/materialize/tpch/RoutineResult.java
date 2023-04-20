package me.alaneuler.calcite.ng.demo.materialize.tpch;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.calcite.rel.RelNode;

@Data
@AllArgsConstructor
public class RoutineResult {
  private RelNode before;
  private RelNode after;

  private TpchBaseMain.MvRewriteType type;
}
