package com.iodesystems.fn;

import com.iodesystems.fn.thread.Async;
import com.iodesystems.fn.thread.Deferred;
import com.iodesystems.fn.tree.simple.Node;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

public class DocTest {

  @Test
  public void testDocs() {
    //
    for (Integer value : Fn.of(1, 2, 3).not(2).loop(2)) {
      System.out.println(value);
    }

    //
    Map<String, List<Integer>> grouped = Fn.of(1, 2, 3, 2, 3).group(Object::toString);

    //
    Enumeration<Integer> enumeration = Collections.enumeration(Fn.list(1, 2, 3));
    System.out.println("Enumerations!");
    for (Integer value : Fn.of(enumeration)) {
      System.out.print(value);
    }
    System.out.println();

    System.out.println("Sized Buckets!");
    SizedIndexAccessor sizedBucket = new SizedIndexAccessor(Fn.list(1, 2, 3));
    for (Integer value : Fn.of(sizedBucket, SizedIndexAccessor::getSize, SizedIndexAccessor::get)) {
      System.out.print(value);
    }
    System.out.println();

    System.out.println("Next Containers!");
    HasNext linkedItemsRoot = Fn.range(1, 3).reverse().combine(null, HasNext::new);
    for (HasNext hasNext : Fn.of(linkedItemsRoot).withNext(HasNext::getNext)) {
      System.out.print(hasNext.getValue());
    }
    System.out.println();

    //
    ByteArrayInputStream inputStream = new ByteArrayInputStream("asdf".getBytes());
    try {
      String contents = Fn.readFully(inputStream);
      for (String line : Fn.lines(inputStream)) {}
    } catch (IOException ignore) {

    }

    //
    boolean b = Fn.isBlank("") || Fn.isBlank(null); // true

    //
    Fn.split("a_0b_0c_0", "_0").toList();

    //
    Fn.of(Node.v("value")).breadth(Node::getChildren);

    // Asyncronous helpers
    final String[] result = new String[] {null};
    Fn.async(() -> "Hello world!")
        .then(
            new Async.Result<String>() {
              @Override
              public String onResult(String message) {
                result[0] = message;
                return null;
              }
            });

    // Pipeline helpers
    ExecutorService backgroundExecutor = Executors.newFixedThreadPool(1);
    ExecutorService forgroundExecutor = Executors.newFixedThreadPool(1);
    Fl<String> pipeline =
        Fl.of(String.class)
            .on(backgroundExecutor)
            .convert(String::length)
            .when(i -> i % 2 == 0)
            .fork(
                fork ->
                    fork.on(forgroundExecutor)
                        .when(i -> i % 2 == 0)
                        .then(i -> System.out.println(i + " is even")))
            .fork(fork -> fork.when(i -> i % 2 == 1).then(i -> System.out.println(i + " is odd")))
            .build();

    pipeline.submit("data");
  }

  @Test
  public void testDocExtra() {
    final String[] result = new String[] {null};
    final Integer[] progress = new Integer[] {null, null};
    Deferred<String> defer = Fn.defer();
    defer.then(
        new Async.Result<String>() {
          @Override
          public String onResult(String o) {
            result[0] = o;
            return null;
          }

          @Override
          public int onProgress(int deferredProgress) {
            progress[deferredProgress - 1] = deferredProgress;
            return deferredProgress;
          }
        });

    defer.progress(1);
    defer.progress(2);
    defer.result("Hello World!");
  }
}
