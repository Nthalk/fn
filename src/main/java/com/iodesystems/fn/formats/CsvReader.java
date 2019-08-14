package com.iodesystems.fn.formats;

import com.iodesystems.fn.data.CloseableIterable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CsvReader implements CloseableIterable<List<String>> {

  final java.io.Reader r;
  private final char separator;
  private final String quotes;
  private List<String> currentRow = new ArrayList<>();
  private State state = State.QUOTE_OR_FIELD_OR_SEPARATOR_OR_NEWLINE;

  public CsvReader(InputStream inputStream, char separator, String quotes) {
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
