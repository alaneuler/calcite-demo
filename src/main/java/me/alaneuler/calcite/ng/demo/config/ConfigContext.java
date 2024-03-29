package me.alaneuler.calcite.ng.demo.config;

import java.util.Map;
import java.util.Properties;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.plan.Context;

public class ConfigContext implements Context {
  private CalciteConnectionConfig calciteConnectionConfig;

  public ConfigContext(Map<String, String> configs) {
    Properties properties = new Properties();
    properties.putAll(configs);
    this.calciteConnectionConfig = new CalciteConnectionConfigImpl(properties);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> clazz) {
    if (clazz.equals(CalciteConnectionConfig.class)) {
      return (T) this.calciteConnectionConfig;
    }
    return clazz.isInstance(this) ? clazz.cast(this) : null;
  }
}
