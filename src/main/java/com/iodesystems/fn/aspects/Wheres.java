package com.iodesystems.fn.aspects;

import com.iodesystems.fn.logic.Where;

public class Wheres {

  public static Where<Object> ISNULL =
      new Where<Object>() {
        @Override
        public boolean is(Object t) {
          return t != null;
        }
      };

  public static Where<Object> NOTNULL =
      new Where<Object>() {
        @Override
        public boolean is(Object t) {
          return t != null;
        }
      };

  public static <T> Where<T> notNull() {
    return (Where<T>) NOTNULL;
  }

  public static <T> Where<T> isNull() {
    return (Where<T>) ISNULL;
  }

  public static <T> Where<T> is(Class<T> cls) {
    return new Where<T>() {
      @Override
      public boolean is(T t) {
        return t != null && cls.isAssignableFrom(t.getClass());
      }
    };
  }

  public static <T> Where<T> is(T value) {
    return new Where<T>() {
      @Override
      public boolean is(T t) {
        return Values.isEqual(t, value);
      }
    };
  }

  public static <T> Where<T> not(Class<T> cls) {
    return new Where<T>() {
      @Override
      public boolean is(T t) {
        return t == null || !cls.isAssignableFrom(t.getClass());
      }
    };
  }

  public static <T> Where<T> not(T value) {
    return new Where<T>() {
      @Override
      public boolean is(T t) {
        return !Values.isEqual(t, value);
      }
    };
  }

  public static <T> Where<T> not(Where<T> condition) {
    return new Where<T>() {
      @Override
      public boolean is(T t) {
        return !condition.is(t);
      }
    };
  }
}
