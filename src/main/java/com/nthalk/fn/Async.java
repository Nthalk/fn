package com.nthalk.fn;

import com.nthalk.fn.async.AsyncFrom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

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

    public static <A> Async<A> async(Callable<A> initial) {
        return async(EXISTING_THREAD, initial);
    }

    public static <A> Async<A> async(Executor executor, Callable<A> initial) {
        return new Initial<A>(executor, initial);
    }

    private <B> Next<A, B> then(Next<A, B> next) {
        if (result != null) {
            next.onParentResult(result.get(null));
        } else if (exception != null) {
            next.onParentException(exception);
        } else {
            nexts.add(next);
        }
        return next;
    }

    public <B> Async<B> then(Executor executor, AsyncFrom<A, B> from) {
        return then(new Next<A, B>(executor, from));
    }

    public <B> Async<B> then(AsyncFrom<A, B> from) {
        return then(executor, from);
    }

    protected void then(Exception exception) {
        this.exception = exception;
        for (Next<A, ?> next : nexts) {
            next.onParentException(exception);
        }
        nexts.clear();
    }

    protected void then(A result) {
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

        private final AsyncFrom<A, B> from;

        public Next(Executor executor, AsyncFrom<A, B> from) {
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
}
