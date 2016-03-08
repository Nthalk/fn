package com.iodesystems.fn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Async<A> {

    public static final Executor INLINE = new Executor() {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    };
    final Executor executor;
    private final List<Next<A, ?>> nexts = new ArrayList<Next<A, ?>>();
    private Option<A> result = null;
    private Exception exception = null;
    private int progress = -1;

    Async(Executor executor) {
        this.executor = executor;
    }

    public static <A> Async<A> async(Callable<A> initial) {
        return async(INLINE, initial);
    }

    public static <A> Deferred<A> defer() {
        return defer(INLINE);
    }

    public static <A> Deferred<A> defer(Executor executor) {
        return new Deferred<A>(executor);
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
                    if (hasException.compareAndSet(false, true)) {
                        next.onParentException(e);
                    }
                    return Option.empty();
                }
            });
        }
        return next;
    }

    public static <A> Async<List<A>> await(Async<A>... asyncs) {
        return await(INLINE, asyncs);
    }

    synchronized void exceptionInternal(Exception exception) {
        this.exception = exception;
        for (Next<A, ?> next : nexts) {
            next.onParentException(exception);
        }
    }

    private synchronized <B> Next<A, B> then(Next<A, B> next) {
        if (progress >= 0) {
            next.onParentProgress(progress);
        }
        if (result != null) {
            next.onParentResult(result.get(null));
        } else if (exception != null) {
            next.onParentException(exception);
        }
        nexts.add(next);
        return next;
    }

    public <B> Async<B> then(OnResult<A, B> onResult) {
        return then(executor, onResult);
    }

    public <B> Async<B> then(OnResult<A, B> onResult, final OnException<B> onException) {
        return then(executor, onResult, onException);
    }

    public <B> Async<B> then(OnResult<A, B> onResult, final OnException<B> onException, final OnProgress onProgress) {
        return then(executor, onResult, onException, onProgress);
    }

    public <B> Async<B> then(Executor executor, OnResult<A, B> onResult) {
        return then(executor, onResult, null);
    }

    public <B> Async<B> then(Executor executor, OnResult<A, B> onResult, final OnException<B> onException) {
        return then(executor, onResult, onException, null);
    }

    public <B> Async<B> then(Executor executor, final OnResult<A, B> onResult, final OnException<B> onException, final OnProgress onProgress) {
        return then(new Next<A, B>(executor, new From<A, B>() {
            @Override
            public B onResult(A a) throws Exception {
                return onResult.onResult(a);
            }

            @Override
            public int onProgress(int progress) {
                if (onProgress != null) {
                    return onProgress.onProgress(progress);
                }
                return super.onProgress(progress);
            }

            @Override
            public Option<B> onException(Exception e) {
                if (onException != null) {
                    return onException.onException(e);
                }
                return super.onException(e);
            }
        }));
    }

    public Async<A> then(Result<A> from) {
        return then(executor, from);
    }

    public <B> Async<B> then(From<A, B> from) {
        return then(executor, from);
    }

    public Async<A> then(Executor executor, Result<A> result) {
        return then(new Next<A, A>(executor, result));
    }

    public <B> Async<B> then(Executor executor, From<A, B> from) {
        return then(new Next<A, B>(executor, from));
    }

    synchronized void progressInternal(int progress) {
        this.progress = progress;
        for (Next<A, ?> next : nexts) {
            next.onParentProgress(progress);
        }
    }

    synchronized void resultInternal(A result) {
        this.result = Option.of(result);
        for (Next<A, ?> next : nexts) {
            next.onParentResult(result);
        }
    }

    public Async<A> onProgress(OnProgress onProgress) {
        return then(executor, null, null, onProgress);
    }

    public <B> Async<B> onException(OnException<B> onException) {
        return then(executor, null, onException, null);
    }

    public Async<A> onProgress(Executor executor, OnProgress onProgress) {
        return then(executor, null, null, onProgress);
    }

    public <B> Async<B> onException(Executor executor, OnException<B> onException) {
        return then(executor, null, onException, null);
    }

    public interface OnResult<A, B> {
        B onResult(A a) throws Exception;
    }

    public interface OnException<B> {
        Option<B> onException(Exception e);

    }

    public interface OnProgress {
        int onProgress(int progress);
    }

    public static class Deferred<A> extends Async<A> {

        public Deferred(Executor executor) {
            super(executor);
        }

        public void progress(int progress) {
            if (progress > -1) {
                progressInternal(progress);
            }
        }

        public void result(A result) {
            resultInternal(result);
        }

        public void exception(Exception exception) {
            exceptionInternal(exception);
        }
    }

    public static class Initial<A> extends Async<A> {
        private Initial(Executor executor, final Callable<A> callable) {
            super(executor);
            if (executor == INLINE) {
                try {
                    resultInternal(callable.call());
                } catch (Exception e) {
                    exceptionInternal(e);
                }
            } else {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            resultInternal(callable.call());
                        } catch (Exception e) {
                            exceptionInternal(e);
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

        private void onParentProgressInternal(int progress) {
            progressInternal(from.onProgress(progress));
        }

        private void onParentResultInternal(final A result) {
            try {
                resultInternal(from.onResult(result));
            } catch (Exception e) {
                exceptionInternal(e);
            }
        }

        void onParentProgress(final int progress) {
            if (executor == INLINE) {
                onParentProgressInternal(progress);
            } else {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        onParentProgressInternal(progress);
                    }
                });
            }
        }

        void onParentResult(final A result) {
            if (executor == INLINE) {
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

        void onParentException(final Exception exception) {
            if (executor == INLINE) {
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
                    resultInternal(recovery.get());
                } else {
                    exceptionInternal(exception);
                }
            } catch (Exception e) {
                exceptionInternal(e);
            }
        }
    }


    public static abstract class Result<A> extends From<A, A> {
        @Override
        public A onResult(A a) throws Exception {
            return a;
        }
    }

    public static abstract class From<A, B> implements OnResult<A, B>, OnException<B>, OnProgress {
        @Override
        public B onResult(A a) throws Exception {
            return null;
        }

        @Override
        public int onProgress(int progress) {
            return progress;
        }

        @Override
        public Option<B> onException(Exception e) {
            return Option.empty();
        }
    }
}
