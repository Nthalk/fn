package com.iodesystems.fn.aspects;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Exceptions {

  public static String stackString(Throwable e) {
    StringWriter stackString = new StringWriter();
    e.printStackTrace(new PrintWriter(stackString));
    return stackString.toString();
  }
}
