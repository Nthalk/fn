package com.iodesystems.fn.thread;

import static org.junit.Assert.assertEquals;

import com.iodesystems.fn.Fn;
import com.iodesystems.fn.data.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

public class AsyncTest {

  final Executor executor = Executors.newFixedThreadPool(3);

  /** Async can be run in sync style. */
  @Test
  public void testSimpleSyncResult() {
    final String[] result = new String[] {null};

    // Async's without an executor run inline.
    Fn.async(() -> "Hello World!")
        .then(
            new Async.Result<String>() {
              @Override
              public String onResult(String message) {
                // It did it
                result[0] = message;
                return null;
              }
            });

    assertEquals("Hello World!", result[0]);
  }

  /**
   * Async can also run any stage on any Executor, the INLINE Executor has a special path that runs
   * on the calling thread.
   */
  @Test
  public void testSimpleAsyncResult() throws TimeoutException, InterruptedException {
    final int[] result = new int[] {0};
    final Waiter waiter = new Waiter();

    Fn.async(
            executor,
            () -> {
              Thread.sleep(10L);
              return 1;
            })
        .then(
            Async.INLINE,
            new Async.Result<Integer>() {
              @Override
              public Integer onResult(Integer integer) throws Exception {
                result[0] = integer;
                waiter.resume();
                return super.onResult(integer);
              }
            });

    waiter.await(100L);
    assertEquals(1, result[0]);
  }

  /** Asyncs can convert and branch, even on separate Executors */
  @Test
  public void testBranchingAndConvertingAsync() throws TimeoutException, InterruptedException {
    final Waiter waiter = new Waiter();
    Async<Integer> source = Fn.async(() -> 1);

    source.then(
        new Async.From<Integer, String>() {
          @Override
          public String onResult(Integer integer) {
            waiter.assertEquals(1, integer);
            waiter.resume();
            return integer.toString();
          }
        });

    source
        .then(
            executor,
            integer -> {
              waiter.assertEquals(1, integer);
              waiter.resume();
              return integer.doubleValue();
            })
        .then(
            new Async.Result<Double>() {
              @Override
              public Double onResult(Double aDouble) throws Exception {
                waiter.assertEquals(1d, aDouble);
                waiter.resume();
                return super.onResult(aDouble);
              }
            });
    waiter.await(300L, 3);
  }

  /** Sometimes you need to have a deferred object, instead of a callable */
  @Test
  public void testSimpleDeferred() {
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
    assertEquals(result[0], "Hello World!");
    assertEquals(progress[0], new Integer(1));
    assertEquals(progress[1], new Integer(2));
  }

  /**
   * Promise chains are really trees, and those trees can be reusable. Deferred objects can be fired
   * as many times as necessary.
   */
  @Test
  public void testReusableDeferred() throws TimeoutException, InterruptedException {
    final Waiter waiter = new Waiter();
    Deferred<Object> defer = Fn.defer();
    defer.result(1);
    defer.then(
        o -> {
          waiter.resume();
          return null;
        });
    defer.result(2);
    defer.result(3);
    waiter.await(300L, 3);
  }

  /**
   * It is useful to have the ability to recover from an exception, when an onException method
   * returns a filled Option, then the contents of that option is used, if the option is empty, the
   * exception is passed down to the child async objects.
   */
  @Test
  public void testAsyncExceptionRecovery() throws TimeoutException, InterruptedException {
    final Waiter waiter = new Waiter();

    Fn.async(
            executor,
            () -> {
              throw new Exception();
            })
        .onException(e -> Option.of(1))
        .then(
            integer -> {
              waiter.assertEquals(1, integer);
              waiter.resume();
              return null;
            });

    waiter.await(300L);
  }

  @Test
  public void testExecutorAsyncAffinity() {
    CountingExecutor countingExecutor = new CountingExecutor();
    CountingExecutor secondCountingExecutor = new CountingExecutor();
    Async<String> root = Async.async(countingExecutor, () -> "heyo");
    root.then(
            new Async.Result<String>() {
              // Since the this branch starts off of the same countingExcutor, it will be executed
              // inline
            })
        .then(
            secondCountingExecutor,
            new Async.Result<String>() {
              // However, this branch swiches executors, so this will be submitted as a runnable
            })
        .then(
            secondCountingExecutor,
            new Async.Result<String>() {
              // Again, branched from same executor as parent, so no re-submit
            });

    root.then(
            new Async.Result<String>() {
              // Since the this branch starts off of the same countingExcutor, it will be executed
              // inline
            })
        .then(
            secondCountingExecutor,
            new Async.Result<String>() {
              // However, this branch swiches executors, so this will be submitted as a runnable
            })
        .then(
            Async.INLINE,
            new Async.Result<String>() {
              // Because we specify INLINE, we will just run inline on the existing thread.
            });

    assertEquals(1, countingExecutor.getCount());
    assertEquals(2, secondCountingExecutor.getCount());
  }

  @Test
  public void testExecutorDeferAffinity() {
    CountingExecutor countingExecutor = new CountingExecutor();
    CountingExecutor secondCountingExecutor = new CountingExecutor();
    Deferred<String> root = Async.defer(countingExecutor);

    root.then(
            countingExecutor,
            new Async.Result<String>() {
              // Since the this branch starts off of the same countingExcutor, it will be executed
              // inline
            })
        .then(
            secondCountingExecutor,
            new Async.Result<String>() {
              // However, this branch swiches executors, so this will be submitted as a runnable
            })
        .then(
            secondCountingExecutor,
            new Async.Result<String>() {
              // Again, branched from same executor as parent, so no re-submit
            });

    // Calling this here should not matter
    root.result("heyo");

    root.then(
            countingExecutor,
            new Async.Result<String>() {
              // Since the this branch starts off of the same countingExcutor, it will be executed
              // inline
            })
        .then(
            secondCountingExecutor,
            new Async.Result<String>() {
              // However, this branch swiches executors, so this will be submitted as a runnable
            })
        .then(
            Async.INLINE,
            new Async.Result<String>() {
              // Because we specify INLINE, we will just run inline on the existing thread.
            });

    assertEquals(2, countingExecutor.getCount());
    assertEquals(2, secondCountingExecutor.getCount());

    // Again, these chained branches are repeatable
    root.result("heyo2");
    assertEquals(4, countingExecutor.getCount());
    assertEquals(4, secondCountingExecutor.getCount());
  }

  @Test
  public void testAsync() throws TimeoutException, InterruptedException {
    final Waiter waiter = new Waiter();

    Async<String> async = Async.async(executor, () -> "5");

    async
        .then(
            new Async.From<String, Integer>() {
              @Override
              public Integer onResult(String a) throws Exception {
                Thread.sleep(10);
                throw new Exception("lame");
              }
            })
        .then(
            new Async.Result<Integer>() {
              @Override
              public Option<Integer> onException(Exception e) {
                return Option.of(4);
              }
            })
        .then(
            new Async.Result<Integer>() {
              @Override
              public Integer onResult(Integer integer) throws Exception {
                waiter.assertEquals(4, integer);
                waiter.resume();
                return super.onResult(integer);
              }
            });

    async.then(
        new Async.From<String, Integer>() {
          @Override
          public Integer onResult(String a) {
            waiter.assertEquals("5", a);
            waiter.resume();
            return 0;
          }
        });
    waiter.await(100L, 2);
  }

  @Test
  public void testOnProgress() {
    Deferred<Object> defer = Fn.defer();
    defer
        .onProgress(
            progress -> {
              assertEquals(1, progress);
              // Modification of the progress propagates it to downstream Asyncs.
              return progress + 1;
            })
        .onProgress(
            progress -> {
              assertEquals(2, progress);
              // -1 prevents progress from propagating
              return -1;
            });
    defer.progress(1);
  }

  @Test
  public void testAwaitDeferred() throws TimeoutException, InterruptedException {
    final Waiter waiter = new Waiter();
    Deferred<Integer> defer = Fn.defer(executor);
    defer
        .then(integer -> integer + 1)
        .then(
            integer -> {
              waiter.assertEquals(2, integer);
              waiter.resume();
              return null;
            });

    defer.result(1);
    waiter.await(1000L);
  }

  @Test
  public void testAwait() {
    Fn.when(Fn.async((Callable<Object>) () -> 1), Fn.async(() -> 2), Fn.async(() -> 3))
        .then(
            new Async.Result<List<Object>>() {
              @Override
              public List<Object> onResult(List<Object> objects) throws Exception {
                assertEquals(Arrays.asList(1, 2, 3), objects);
                return super.onResult(objects);
              }
            });
  }

  @Test
  public void testThreadedAwait() throws Exception {
    final Random random = new Random();
    final Waiter waiter = new Waiter();
    Fn.when(
            executor,
            Fn.async(
                executor,
                (Callable<Object>)
                    () -> {
                      Thread.sleep(random.nextInt(20));
                      return 1;
                    }),
            Fn.async(
                executor,
                () -> {
                  Thread.sleep(random.nextInt(20));
                  return 2;
                }),
            Fn.async(
                executor,
                () -> {
                  Thread.sleep(random.nextInt(20));
                  return 3;
                }))
        .then(
            executor,
            new Async.Result<List<Object>>() {
              @Override
              public List<Object> onResult(List<Object> objects) throws Exception {
                waiter.assertEquals(Arrays.asList(1, 2, 3), objects);
                waiter.resume();
                return super.onResult(objects);
              }
            });
    waiter.await(100L, 1);
  }

  public static class CountingExecutor implements Executor {

    int count = 0;

    @Override
    public void execute(Runnable runnable) {
      count++;
      runnable.run();
    }

    public int getCount() {
      return count;
    }
  }
}
