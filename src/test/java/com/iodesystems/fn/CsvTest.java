package com.iodesystems.fn;

import com.iodesystems.fn.formats.CsvWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class CsvTest {

  @Test
  public void testRowVariants() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    CsvWriter csvWriter = new CsvWriter(buffer);
    csvWriter.writeRow((List<String>) null);
    csvWriter.writeRow((String[]) null);
    csvWriter.writeRow(Fn.list("a", "b", "c", null, ",", "\""));
    csvWriter.writeRow("a", "b", "c", null, ",", "\"");
    csvWriter.writeRow(new String[] {"a", "b", "c"});
    csvWriter.close();
    Fn<List<String>> data = Fn.fromCsv(buffer.toString());
    Assert.assertEquals(
        Fn.list(
            Fn.list("a", "b", "c", "", ",", "\""),
            Fn.list("a", "b", "c", "", ",", "\""),
            Fn.list("a", "b", "c")),
        data.list());
  }

  @Test
  public void testLargeCsv() throws IOException {
    int rows = 10000;
    int cells = 25;

    Fn<List<String>> data =
        Fn.range(1, rows).convert(i -> Fn.range(1, cells).convert(Object::toString).list());

    PipedInputStream pis = new PipedInputStream();
    PipedOutputStream pos = new PipedOutputStream();
    pos.connect(pis);

    new Thread(
            () -> {
              try {
                Fn.toCsv(data, pos);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .start();

    Fn<List<String>> pipedData = Fn.fromCsv(pis);
    Integer combine = pipedData.combine(0, (r, i) -> i + r.size());
    Assert.assertEquals((Integer) (rows * cells), combine);
    pipedData.close();
  }

  @Test
  public void testEscapes() throws IOException {
    Fn<List<String>> data = Fn.fromCsv(Fn.resource("escapes.csv").get()).cache();
    Assert.assertEquals(
        Fn.list(Fn.list("\\\\", "\\", "\""), Fn.list("\\\\", "\\", "\"")), data.list());
    Assert.assertEquals(data.list(), Fn.fromCsv(Fn.toCsv(data)).list());
  }

  @Test
  public void strangeField() throws IOException {
    String csv = Fn.readFully(Fn.resource("strangeQuotes.csv").get());
    Fn<List<String>> parsed = Fn.fromCsv(csv).cache();

    Assert.assertEquals(Fn.list(Fn.list("", ","), Fn.list("a", "")), parsed.list());

    Assert.assertEquals(parsed.list(), Fn.fromCsv(Fn.toCsv(parsed)).toList());
  }

  @Test
  public void garbageCsv() throws IOException {
    String csv = Fn.readFully(Fn.resource("garbage.csv").get());
    Fn<List<String>> data = Fn.fromCsv(csv).cache();
    Assert.assertEquals(data.list(), Fn.fromCsv(Fn.toCsv(data)).list());
  }
}
