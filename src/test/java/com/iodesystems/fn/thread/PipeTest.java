package com.iodesystems.fn.thread;

import com.iodesystems.fn.Fl;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

public class PipeTest {

  private void print(String msg) {
    System.out.println(Thread.currentThread().getName() + msg);
  }

  @Test
  public void test() throws InterruptedException {
    ExecutorService ui = Executors.newFixedThreadPool(1);
    ExecutorService bg = Executors.newFixedThreadPool(1);
    Fl<String> source =
        Fl.of(String.class)
            .on(bg)
            .convert(
                s -> {
                  print("(bg convert):" + s);
                  return s.length();
                })
            .then(l -> print("(bg handle):" + l))
            .on(ui)
            .convert(
                s -> {
                  print("(ui convert):" + s);
                  return String.valueOf(s);
                })
            .on(bg)
            .fork(
                f ->
                    f.when(
                            e -> {
                              print("(bg when):");
                              return true;
                            })
                        .then(l -> print("(bg when-handle):" + l)))
            .then(l -> print("(bg handle):" + l))
            .build();
    source.submit("e");
    Thread.sleep(1000);
  }
}
