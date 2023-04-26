package me.alaneuler.calcite.ng.demo.rules;

import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelRule;
import org.apache.calcite.rel.rules.TransformationRule;
import org.immutables.value.Value;

@Value.Enclosing
public class MyRule extends RelRule<MyRule.Config> implements TransformationRule {
  protected MyRule(Config config) {
    super(config);
  }

  @Override
  public void onMatch(RelOptRuleCall call) {

  }

  @Value.Immutable
  public interface Config extends RelRule.Config {
    Config DEFAULT = ImmutableMyRule.Config.builder().build();
  }
}
