package com.nthalk.fn;

import com.nthalk.fn.async.AsyncFrom;
import com.nthalk.fn.async.AsyncResult;
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
        async.then(new AsyncFrom<String, Integer>() {
            @Override
            public Integer onResult(String a) throws Exception {
                throw new Exception("lame");
            }
        }).then(new AsyncResult<Integer>() {
            @Override
            public Option<Void> onException(Exception e) {
                return super.onException(e);
            }
        });

        async.then(new AsyncFrom<String, Integer>() {
            @Override
            public Integer onResult(String a) {
                return a == null ? 0 : a.length();
            }
        });
    }

}
