package com.iodesystems.fn.aspects;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class Strings {

  public static String ifBlank(String thing, String ifBlank) {
    return isBlank(thing) ? ifBlank : thing;
  }

  public static boolean isBlank(String str) {
    return str == null || str.length() == 0;
  }

  public static Iterable<String> lines(String input) {
    try {
      return lines(new ByteArrayInputStream(input.getBytes()));
    } catch (IOException e) {
      throw new IllegalStateException("this should not happen", e);
    }
  }

  public static Iterable<String> lines(InputStream ios) throws IOException {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(ios));
    final String first = reader.readLine();
    return () ->
        new Iterator<String>() {
          String next = first;

          @Override
          public void remove() {}

          @Override
          public boolean hasNext() {
            return next != null;
          }

          @Override
          public String next() {
            String tmp = next;
            try {
              next = reader.readLine();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            return tmp;
          }
        };
  }

  public static String readFully(InputStream ios) throws IOException {
    char[] buffer = new char[4096];
    BufferedReader reader = new BufferedReader(new InputStreamReader(ios));
    StringBuilder result = new StringBuilder();
    int len;
    while ((len = reader.read(buffer)) != -1) {
      result.append(buffer, 0, len);
    }
    return result.toString();
  }

  public static Iterable<String> split(final String target, final String on) {
    return split(target, on, null);
  }

  public static Iterable<String> split(final String target, final String on, Integer limit) {
    return () ->
        new Iterator<String>() {
          int nextIndex = target.indexOf(on);
          int lastIndex = 0;
          int count = 0;

          @Override
          public boolean hasNext() {
            return lastIndex != -1;
          }

          @Override
          public String next() {
            String tmp;
            if (nextIndex == -1 || (limit != null && ++count == limit)) {
              tmp = target.substring(lastIndex);
              lastIndex = -1;
            } else {
              tmp = target.substring(lastIndex, nextIndex);
              lastIndex = nextIndex + on.length();
              nextIndex = target.indexOf(on, lastIndex);
            }
            return tmp;
          }

          @Override
          public void remove() {}
        };
  }
}
