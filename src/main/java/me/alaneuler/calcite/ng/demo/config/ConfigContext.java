package me.alaneuler.calcite.ng.demo.config;

import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.plan.Context;

import java.util.Map;
import java.util.Properties;

public class ConfigContext implements Context {
  private CalciteConnectionConfig calciteConnectionConfig;

  public ConfigContext(Map<String, String> config) {
    Properties properties = new Properties();
    properties.putAll(config);
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
