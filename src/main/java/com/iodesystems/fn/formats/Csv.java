package com.iodesystems.fn.formats;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Csv {

  public static class Writer implements Closeable {

    private final char separator;
    private final char quote;
    private final OutputStreamWriter output;
    private final StringBuilder buffer = new StringBuilder();

    public Writer(char separator, char quote, OutputStream outputStream) {
      this.separator = separator;
      this.quote = quote;
      this.output = new OutputStreamWriter(outputStream);
    }

    public Writer(OutputStream output) {
      this(',', '\"', output);
    }

    public void write(List<String> row) throws IOException {
      if (row == null) {
        output.write("\n");
      } else {
        for (int i = 0; i < row.size(); i++) {
          String value = row.get(i);
          if (value.indexOf('\n') != -1
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
          } else if (value.length() != 0) {
            output.write(value);
          }
          if (i != row.size() - 1) {
            output.write(separator);
          }
        }
        output.write('\n');
      }
    }

    public void write(Iterable<List<String>> rows) throws IOException {
      for (List<String> row : rows) {
        write(row);
      }
    }

    @Override
    public void close() throws IOException {
      output.flush();
      output.close();
    }
  }

  public static class Reader implements Iterable<List<String>>, Closeable {

    final java.io.Reader r;
    private final char separator;
    private final String quotes;
    private List<String> currentRow = new ArrayList<>();
    private State state = State.QUOTE_OR_FIELD_OR_SEPARATOR_OR_NEWLINE;

    public Reader(InputStream inputStream, char separator, String quotes) {
      r = new InputStreamReader(inputStream);
      this.separator = separator;
      this.quotes = quotes;
    }

    @Override
    public Iterator<List<String>> iterator() {

      final StringBuilder buffer = new StringBuilder();
      return new Iterator<List<String>>() {

        public boolean fillRow(List<String> row) throws IOException {
          state = State.QUOTE_OR_FIELD_OR_SEPARATOR_OR_NEWLINE;
          buffer.setLength(0);
          char quote = 0;
          boolean lastCharWasQuote = false;
          while (true) {
            char c = (char) r.read();

            if (c == 0xffff) {
              if (row.isEmpty()) {
                return false;
              } else if (buffer.length() != 0) {
                row.add(buffer.toString().trim());
              } else {
                row.add("");
              }
              return true;
            } else {
              switch (state) {
                case QUOTE_OR_FIELD_OR_SEPARATOR_OR_NEWLINE:
                  if (c == separator) {
                    row.add("");
                  } else if (c == '\n') {
                    if (!row.isEmpty()) {
                      row.add("");
                      return true;
                    }
                  } else if (Character.isWhitespace(c)) {
                    // ignored
                  } else if (quotes.indexOf(c) != -1) {
                    quote = c;
                    state = State.QUOTED_FIELD;
                  } else {
                    buffer.append(c);
                    state = State.FIELD;
                  }
                  break;
                case QUOTED_FIELD:
                  if (c == quote) {
                    if (lastCharWasQuote) {
                      buffer.append(quote);
                      lastCharWasQuote = false;
                    } else {
                      lastCharWasQuote = true;
                    }
                  } else if (lastCharWasQuote) {
                    lastCharWasQuote = false;
                    // We are out!
                    row.add(buffer.toString());
                    buffer.setLength(0);
                    if (c == separator) {
                      state = State.QUOTE_OR_FIELD_OR_SEPARATOR_OR_NEWLINE;
                    } else if (c == '\n') {
                      return true;
                    } else {
                      state = State.SEPARATOR_OR_NEWLINE;
                    }
                  } else {
                    lastCharWasQuote = false;
                    buffer.append(c);
                  }
                  break;
                case FIELD:
                  if (c == '\n') {
                    row.add(buffer.toString().trim());
                    return true;
                  } else if (c == separator) {
                    row.add(buffer.toString().trim());
                    buffer.setLength(0);
                    state = State.QUOTE_OR_FIELD_OR_SEPARATOR_OR_NEWLINE;
                  } else {
                    buffer.append(c);
                  }
                  break;
                case SEPARATOR_OR_NEWLINE:
                  if (c == separator) {
                    state = State.QUOTE_OR_FIELD_OR_SEPARATOR_OR_NEWLINE;
                  } else if (c == '\n') {
                    if (!row.isEmpty()) {
                      return true;
                    }
                  }
                  break;
              }
            }
          }
        }

        @Override
        public boolean hasNext() {
          try {
            currentRow = new ArrayList<>();
            return fillRow(currentRow);
          } catch (IOException e) {
            throw new RuntimeException("Could not read csv data", e);
          }
        }

        @Override
        public List<String> next() {
          return currentRow;
        }
      };
    }

    @Override
    public void close() throws IOException {
      r.close();
    }

    enum State {
      QUOTE_OR_FIELD_OR_SEPARATOR_OR_NEWLINE,
      QUOTED_FIELD,
      FIELD,
      SEPARATOR_OR_NEWLINE
    }
  }
}
