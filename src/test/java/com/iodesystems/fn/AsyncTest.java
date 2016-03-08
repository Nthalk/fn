package com.iodesystems.fn;

import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

public class AsyncTest {

    Executor executor = Executors.newFixedThreadPool(3);

    /**
     * Async can be run in sync style.
     */
    @Test
    public void testSimpleSyncResult() {
        final int[] result = new int[]{0};

        // Async's without an executor run inline.
        Fn.async(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        }).then(new Async.Result<Integer>() {
            @Override
            public Integer onResult(Integer integer) throws Exception {
                // It did it
                result[0] = integer;
                return super.onResult(integer);
            }
        });

        assertEquals(1, result[0]);
    }

    /**
     * Async can also run any stage on any Executor, the INLINE Executor has a special path
     * that runs on the calling thread.
     */
    @Test
    public void testSimpleAsyncResult() throws TimeoutException {
        final int[] result = new int[]{0};
        final Waiter waiter = new Waiter();

        Fn.async(executor, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(10L);
                return 1;
            }
        }).then(Async.INLINE, new Async.Result<Integer>() {
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

    /**
     * Asyncs can convert and branch, even on separate Executors
     */
    @Test
    public void testBranchingAndConvertingAsync() throws TimeoutException {
        final Waiter waiter = new Waiter();
        Async<Integer> source = Fn.async(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        });

        source.then(new Async.From<Integer, String>() {
            @Override
            public String onResult(Integer integer) throws Exception {
                waiter.assertEquals(1, integer);
                waiter.resume();
                return integer.toString();
            }
        });

        source.then(executor, new Async.OnResult<Integer, Double>() {
            @Override
            public Double onResult(Integer integer) throws Exception {
                waiter.assertEquals(1, integer);
                waiter.resume();
                return integer.doubleValue();
            }
        }).then(new Async.Result<Double>() {
            @Override
            public Double onResult(Double aDouble) throws Exception {
                waiter.assertEquals(1d, aDouble);
                waiter.resume();
                return super.onResult(aDouble);
            }
        });
        waiter.await(300L, 3);
    }

    /**
     * Sometimes you need to have a deferred object, instead of a callable
     */
    @Test
    public void testSimpleDeferred() throws TimeoutException {
        final Waiter waiter = new Waiter();
        Async.Deferred<Object> defer = Fn.defer(executor);

        defer.then(new Async.Result<Object>() {
            @Override
            public Object onResult(Object o) throws Exception {
                waiter.resume();
                return super.onResult(o);
            }

            @Override
            public int onProgress(int progress) {
                waiter.resume();
                return super.onProgress(progress);
            }
        });

        defer.progress(1);
        defer.progress(2);
        defer.result(null);

        waiter.await(100L, 3);
    }

    /**
     * Promise chains are really trees, and those trees can be reusable.
     * Deferred objects can be fired as many times as necessary.
     */
    @Test
    public void testReusableDeferred() throws TimeoutException {
        final Waiter waiter = new Waiter();
        Async.Deferred<Object> defer = Fn.defer();
        defer.result(1);
        defer.then(new Async.OnResult<Object, Object>() {
            @Override
            public Object onResult(Object o) throws Exception {
                waiter.resume();
                return null;
            }
        });

        defer.result(2);
        defer.result(3);

        waiter.await(300L, 3);
    }


    /**
     * It is useful to have the ability to recover from an exception, when an onException method returns
     * a filled Option, then the contents of that option is used, if the option is empty, the exception is
     * passed down to the child async objects.
     */
    @Test
    public void testAsyncExceptionRecovery() throws TimeoutException {
        final Waiter waiter = new Waiter();

        Fn.async(executor, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                throw new Exception();
            }
        }).onException(new Async.OnException<Integer>() {
            @Override
            public Option<Integer> onException(Exception e) {
                return Option.of(1);
            }
        }).then(new Async.OnResult<Integer, Object>() {
            @Override
            public Object onResult(Integer integer) throws Exception {
                waiter.assertEquals(1, integer);
                waiter.resume();
                return null;
            }
        });

        waiter.await(300L);
    }

    @Test
    public void testAsync() throws TimeoutException {
        final Waiter waiter = new Waiter();

        Async<String> async = Async.async(executor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "5";
            }
        });

        async.then(new Async.From<String, Integer>() {
            @Override
            public Integer onResult(String a) throws Exception {
                Thread.sleep(10);
                throw new Exception("lame");
            }
        }).then(new Async.Result<Integer>() {
            @Override
            public Option<Integer> onException(Exception e) {
                return Option.of(4);
            }
        }).then(new Async.Result<Integer>() {
            @Override
            public Integer onResult(Integer integer) throws Exception {
                waiter.assertEquals(4, integer);
                waiter.resume();
                return super.onResult(integer);
            }
        });

        async.then(new Async.From<String, Integer>() {
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
        Async.Deferred<Object> defer = Fn.defer();
        defer.onProgress(new Async.OnProgress() {
            @Override
            public int onProgress(int progress) {
                assertEquals(1, progress);
                // Modification of the progress propagates it to downstream Asyncs.
                return progress + 1;
            }
        }).onProgress(new Async.OnProgress() {
            @Override
            public int onProgress(int progress) {
                assertEquals(2, progress);
                // -1 prevents progress from propagating
                return -1;
            }
        });
        defer.progress(1);
    }

    @Test
    public void testAwaitDeferred() throws TimeoutException {
        final Waiter waiter = new Waiter();
        Async.Deferred<Integer> defer = Fn.defer(executor);
        defer.then(new Async.OnResult<Integer, Integer>() {
            @Override
            public Integer onResult(Integer integer) throws Exception {
                return integer + 1;
            }
        }).then(new Async.OnResult<Integer, Object>() {
            @Override
            public Object onResult(Integer integer) throws Exception {
                waiter.assertEquals(2, integer);
                waiter.resume();
                return null;
            }
        });

        defer.result(1);
        waiter.await(1000L);
    }

    @Test
    public void testAwait() {
        Fn.await(Fn.async(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }), Fn.async(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 2;
            }
        }), Fn.async(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 3;
            }
        })).then(new Async.Result<List<Object>>() {
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
        Fn.await(executor, Fn.async(executor, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Thread.sleep(random.nextInt(20));
                return 1;
            }
        }), Fn.async(executor, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Thread.sleep(random.nextInt(20));
                return 2;
            }
        }), Fn.async(executor, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Thread.sleep(random.nextInt(20));
                return 3;
            }
        })).then(executor, new Async.Result<List<Object>>() {
            @Override
            public List<Object> onResult(List<Object> objects) throws Exception {
                waiter.assertEquals(Arrays.asList(1, 2, 3), objects);
                waiter.resume();
                return super.onResult(objects);
            }
        });
        waiter.await(100L, 1);
    }
}
