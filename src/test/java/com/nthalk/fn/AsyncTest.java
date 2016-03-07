package com.nthalk.fn;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTest {

    @Test
    public void testAsync() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Async<String> async = Async.async(executorService, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "5";
            }
        });

        async.then(new Async.From<String, Integer>() {
            @Override
            public Integer onResult(String a) throws Exception {
                Thread.sleep(100);
                throw new Exception("lame");
            }
        }).then(new Async.Result<Integer>() {
            @Override
            public Option<Integer> onException(Exception e) {
                return Option.of(4);
            }
        });

        async.then(new Async.From<String, Integer>() {
            @Override
            public Integer onResult(String a) {
                return a == null ? 0 : a.length();
            }
        });
    }

}
