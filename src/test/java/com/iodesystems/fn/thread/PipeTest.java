package com.iodesystems.fn.thread;

import com.iodesystems.fn.Fl;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

public class PipeTest {
  @Test
  public void test() throws InterruptedException {
    ExecutorService ui = Executors.newFixedThreadPool(1);
    ExecutorService bg = Executors.newFixedThreadPool(1);
    Fl<String> source =
        Fl.of(String.class)
            .on(bg)
            .convert(
                s -> {
                  System.out.println(Thread.currentThread().getName() + "(bg convert):" + s);
                  return s.length();
                })
            .handle(l -> System.out.println(Thread.currentThread().getName() + "(bg handle):" + l))
            .on(ui)
            .convert(
                s -> {
                  System.out.println(Thread.currentThread().getName() + "(ui convert):" + s);
                  return String.valueOf(s);
                })
            .on(bg)
            .fork(
                f ->
                    f.when(
                            e -> {
                              System.out.println(Thread.currentThread().getName() + "(bg when):");
                              return true;
                            })
                        .handle(
                            l ->
                                System.out.println(
                                    Thread.currentThread().getName() + "(bg when-handle):" + l)))
            .handle(l -> System.out.println(Thread.currentThread().getName() + "(bg handle):" + l))
            .build();
    source.submit("e");
    Thread.sleep(1000);
  }
}
