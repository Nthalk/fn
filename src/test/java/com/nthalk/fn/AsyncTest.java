package com.nthalk.fn;

import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class AsyncTest {

    Executor executor = Executors.newFixedThreadPool(3);

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
    public void testAwaitSuccess() throws Exception {
        final Random random = new Random();
        final Waiter waiter = new Waiter();
        int runs = 1000;
        for (int i = 0; i < runs; i++) {
            Fn.await(Fn.async(executor, new Callable<Object>() {
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
                    waiter.assertEquals(objects, Arrays.asList(1, 2, 3));
                    waiter.resume();
                    return super.onResult(objects);
                }
            });
        }
        waiter.await(100L * runs, runs);
    }
}
