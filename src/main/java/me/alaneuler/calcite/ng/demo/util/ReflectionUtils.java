package me.alaneuler.calcite.ng.demo.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
  public static Field getField(Object o, String fieldName) {
    Class<?> klass = o.getClass();
    while (klass != null) {
      try {
        Field f = klass.getDeclaredField(fieldName);
        f.setAccessible(true);
        return f;
      } catch (NoSuchFieldException e) {
        klass = klass.getSuperclass();
      }
    }
    return null;
  }

  public static void setField(Object target, Field field, Object value) {
    try {
      field.set(target, value);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
