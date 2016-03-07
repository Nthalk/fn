package com.nthalk.fn.async;

import com.nthalk.fn.Option;

public abstract class AsyncFrom<A, B> {

    public B onResult(A a) throws Exception {
        return null;
    }

    public Option<B> onException(Exception e) {
        return Option.empty();
    }
}
