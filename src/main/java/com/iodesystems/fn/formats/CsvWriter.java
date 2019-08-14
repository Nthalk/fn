package com.iodesystems.fn.formats;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CsvWriter implements Closeable {

  private final char separator;
  private final char quote;
  private final OutputStreamWriter output;
  private final StringBuilder buffer = new StringBuilder();

  public CsvWriter(char separator, char quote, OutputStream outputStream) {
    this.separator = separator;
    this.quote = quote;
    this.output = new OutputStreamWriter(outputStream);
  }

  public CsvWriter(OutputStream output) {
    this(',', '\"', output);
  }

  public void writeRow(String... row) throws IOException {
    if (row == null) {
      output.write("\n");
    } else {
      boolean didWrite = false;
      for (String value : row) {
        if (!didWrite) {
          didWrite = true;
        } else {
          output.write(separator);
        }
        if (value == null) {
          // ignore
        } else if (value.indexOf('\n') != -1
            || value.indexOf(separator) != -1
            || value.indexOf(quote) != -1) {
          buffer.setLength(0);
          buffer.append(quote);
          for (char c : value.toCharArray()) {
            if (c == quote) {
              buffer.append(quote);
              buffer.append(quote);
            } else {
              buffer.append(c);
            }
          }
          buffer.append(quote);
          output.write(buffer.toString());
        } else {
          output.write(value);
        }
      }
    }
    output.write('\n');
  }

  public void writeRow(Iterable<String> row) throws IOException {
    if (row == null) {
      output.write("\n");
    } else {
      boolean didWrite = false;
      for (String value : row) {
        if (!didWrite) {
          didWrite = true;
        } else {
          output.write(separator);
        }
        if (value == null) {
          // ignore
        } else if (value.indexOf('\n') != -1
            || value.indexOf(separator) != -1
            || value.indexOf(quote) != -1) {
          buffer.setLength(0);
          buffer.append(quote);
          for (char c : value.toCharArray()) {
            if (c == quote) {
              buffer.append(quote);
              buffer.append(quote);
            } else {
              buffer.append(c);
            }
          }
          buffer.append(quote);
          output.write(buffer.toString());
        } else {
          output.write(value);
        }
      }
    }
    output.write('\n');
  }

  public void writeRows(Iterable<? extends Iterable<String>> rows) throws IOException {
    for (Iterable<String> row : rows) {
      writeRow(row);
    }
  }

  @Override
  public void close() throws IOException {
    output.flush();
    output.close();
  }
}
