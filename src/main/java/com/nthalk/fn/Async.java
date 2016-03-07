package com.nthalk.fn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Async<A> {

    public static final Executor EXISTING_THREAD = new Executor() {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    };
    protected final Executor executor;
    private final List<Next<A, ?>> nexts = new ArrayList<Next<A, ?>>();
    private Option<A> result = null;
    private Exception exception = null;

    public Async(Executor executor) {
        this.executor = executor;
    }

    protected synchronized void then(Exception exception) {
        this.exception = exception;
        for (Next<A, ?> next : nexts) {
            next.onParentException(exception);
        }
        nexts.clear();
    }

    public static <A> Async<A> async(Callable<A> initial) {
        return async(EXISTING_THREAD, initial);
    }

    public static <A> Async<A> async(Executor executor, Callable<A> initial) {
        return new Initial<A>(executor, initial);
    }

    public static <A> Async<List<A>> await(Executor executor, Async<A>... asyncs) {
        final List<A> results = new ArrayList<A>(asyncs.length);
        final AtomicInteger countdown = new AtomicInteger(asyncs.length);
        final AtomicBoolean hasException = new AtomicBoolean(false);
        int index = 0;

        final Next<List<A>, List<A>> next = new Next<List<A>, List<A>>(executor, new Result<List<A>>() {
        });

        for (Async<A> async : asyncs) {
            final int asyncIndex = index++;
            results.add(null);
            async.then(executor, new Result<A>() {
                @Override
                public A onResult(A a) throws Exception {
                    results.set(asyncIndex, a);
                    if (countdown.decrementAndGet() == 0) {
                        next.onParentResult(results);
                    }
                    return super.onResult(a);
                }

                @Override
                public Option<A> onException(Exception e) {
                    if (!hasException.compareAndSet(false, true)) {
                        next.onParentException(e);
                    }
                    return Option.empty();
                }
            });
        }
        return next;
    }

    public static <A> Async<List<A>> await(Async<A>... asyncs) {
        return await(EXISTING_THREAD, asyncs);
    }

    private synchronized <B> Next<A, B> then(Next<A, B> next) {
        if (result != null) {
            next.onParentResult(result.get(null));
        } else if (exception != null) {
            next.onParentException(exception);
        } else {
            nexts.add(next);
        }
        return next;
    }

    public <B> Async<B> then(Executor executor, From<A, B> from) {
        return then(new Next<A, B>(executor, from));
    }

    public <B> Async<B> then(From<A, B> from) {
        return then(executor, from);
    }

    protected synchronized void then(A result) {
        this.result = Option.of(result);
        for (Next<A, ?> next : nexts) {
            next.onParentResult(result);
        }
        nexts.clear();
    }

    public static class Initial<A> extends Async<A> {
        private Initial(Executor executor, final Callable<A> callable) {
            super(executor);
            if (executor == EXISTING_THREAD) {
                try {
                    then(callable.call());
                } catch (Exception e) {
                    then(e);
                }
            } else {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            then(callable.call());
                        } catch (Exception e) {
                            then(e);
                        }
                    }
                });
            }
        }
    }

    public static class Next<A, B> extends Async<B> {

        private final From<A, B> from;

        public Next(Executor executor, From<A, B> from) {
            super(executor);
            this.from = from;
        }

        private void onParentResultInternal(final A result) {
            try {
                then(from.onResult(result));
            } catch (Exception e) {
                then(e);
            }
        }

        protected void onParentResult(final A result) {
            if (executor == EXISTING_THREAD) {
                onParentResultInternal(result);
            } else {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        onParentResultInternal(result);
                    }
                });
            }

        }

        protected void onParentException(final Exception exception) {
            if (executor == EXISTING_THREAD) {
                onParentExceptionInternal(exception);
            } else {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        onParentExceptionInternal(exception);
                    }
                });
            }
        }

        private void onParentExceptionInternal(Exception exception) {
            try {
                Option<B> recovery = from.onException(exception);
                if (recovery.isPresent()) {
                    then(recovery.get());
                } else {
                    then(exception);
                }
            } catch (Exception e) {
                then(e);
            }
        }
    }

    public static abstract class From<A, B> {

        public B onResult(A a) throws Exception {
            return null;
        }

        public Option<B> onException(Exception e) {
            return Option.empty();
        }
    }

    public static abstract class Result<A> extends From<A, A> {
        public A onResult(A a) throws Exception {
            return a;
        }

        public Option<A> onException(Exception e) {
            return Option.empty();
        }
    }
}
