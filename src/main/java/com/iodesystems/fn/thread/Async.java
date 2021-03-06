package com.iodesystems.fn.thread;

import com.iodesystems.fn.data.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Async<A> {

  public static final Executor INLINE = Runnable::run;
  protected final List<Next<A, ?>> nexts = new ArrayList<>();
  final Executor executor;
  protected Option<A> result = null;
  protected Exception exception = null;
  protected int progress = -1;

  Async(Executor executor) {
    this.executor = executor;
  }

  public static <A> Async<A> async(Callable<A> initial) {
    return async(INLINE, initial);
  }

  public static <A> Async<A> async(final A value) {
    return async(INLINE, () -> value);
  }

  public static <A> Deferred<A> defer() {
    return defer(INLINE);
  }

  public static <A> Deferred<A> defer(Executor executor) {
    return new Deferred<>(executor);
  }

  public static <A> Async<A> async(Executor executor, Callable<A> initial) {
    return new Initial<>(executor, initial);
  }

  @SafeVarargs
  public static <A> Next<List<A>, List<A>> when(final Executor executor, final Async<A>... asyncs) {
    final List<A> results = new ArrayList<>(asyncs.length);
    final List<Next<A, A>> thens = new ArrayList<>();
    final AtomicInteger countdown = new AtomicInteger(asyncs.length);
    final AtomicBoolean hasException = new AtomicBoolean(false);
    int index = 0;

    final Next<List<A>, List<A>> next =
        new Next<List<A>, List<A>>(
            executor,
            new Result<List<A>>() {
              // Ignore this
            },
            null) {
          @Override
          public void remove() {
            for (Next<A, A> then : thens) {
              then.remove();
            }
          }
        };

    for (Async<A> async : asyncs) {
      final int asyncIndex = index++;
      results.add(null);
      thens.add(
          async.then(
              executor,
              new Result<A>() {
                @Override
                public A onResult(A a) throws Exception {
                  results.set(asyncIndex, a);
                  if (countdown.decrementAndGet() == 0) {
                    next.onParentResult(executor, results);
                  }

                  return super.onResult(a);
                }

                @Override
                public Option<A> onException(Exception e) {
                  if (hasException.compareAndSet(false, true)) {
                    next.onParentException(executor, e);
                  }
                  return Option.empty();
                }
              }));
    }
    return next;
  }

  @SafeVarargs
  public static <A> Async<List<A>> when(Async<A>... asyncs) {
    return when(INLINE, asyncs);
  }

  synchronized void exceptionInternal(Executor executor, Exception exception) {
    this.exception = exception;
    for (Next<A, ?> next : nexts) {
      next.onParentException(executor, exception);
    }
  }

  protected synchronized <B> Next<A, B> then(Next<A, B> next) {
    if (progress >= 0) {
      next.onParentProgress(executor, progress);
    }
    if (result != null) {
      next.onParentResult(executor, result.orElse(null));
    } else if (exception != null) {
      next.onParentException(executor, exception);
    }
    nexts.add(next);
    return next;
  }

  public <B> Next<A, B> then(OnResult<A, B> onResult) {
    return then(executor, onResult);
  }

  public <B> Next<A, B> then(OnResult<A, B> onResult, final OnException<B> onException) {
    return then(executor, onResult, onException);
  }

  public <B> Next<A, B> then(
      OnResult<A, B> onResult, final OnException<B> onException, final OnProgress onProgress) {
    return then(executor, onResult, onException, onProgress);
  }

  public <B> Next<A, B> then(Executor executor, OnResult<A, B> onResult) {
    return then(executor, onResult, null);
  }

  public <B> Next<A, B> then(
      Executor executor, OnResult<A, B> onResult, final OnException<B> onException) {
    return then(executor, onResult, onException, null);
  }

  public <B> Next<A, B> then(
      Executor executor,
      final OnResult<A, B> onResult,
      final OnException<B> onException,
      final OnProgress onProgress) {
    return then(
        new Next<>(
            executor,
            new From<A, B>() {
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
            },
            this));
  }

  public Next<A, A> then(Result<A> from) {
    return then(executor, from);
  }

  public <B> Next<A, B> then(From<A, B> from) {
    return then(executor, from);
  }

  public Next<A, A> then(Executor executor, Result<A> result) {
    return then(new Next<>(executor, result, this));
  }

  public <B> Next<A, B> then(Executor executor, From<A, B> from) {
    return then(new Next<>(executor, from, this));
  }

  synchronized void progressInternal(Executor executor, int progress) {
    this.progress = progress;
    for (Next<A, ?> next : nexts) {
      next.onParentProgress(executor, progress);
    }
  }

  synchronized void resultInternal(Executor executor, A result) {
    this.result = Option.of(result);
    for (Next<A, ?> next : nexts) {
      next.onParentResult(executor, result);
    }
  }

  public Next<A, A> onProgress(OnProgress onProgress) {
    return then(executor, null, null, onProgress);
  }

  public <B> Next<A, B> onException(OnException<B> onException) {
    return then(executor, null, onException, null);
  }

  public Next<A, A> onProgress(Executor executor, OnProgress onProgress) {
    return then(executor, null, null, onProgress);
  }

  public <B> Next<A, B> onException(Executor executor, OnException<B> onException) {
    return then(executor, null, onException, null);
  }

  private synchronized void remove(Next<A, ?> next) {
    nexts.remove(next);
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

  public static class Initial<A> extends Async<A> {

    private Initial(final Executor executor, final Callable<A> callable) {
      super(executor);
      if (executor == INLINE) {
        try {
          resultInternal(executor, callable.call());
        } catch (Exception e) {
          exceptionInternal(executor, e);
        }
      } else {
        executor.execute(
            () -> {
              try {
                Initial.this.resultInternal(executor, callable.call());
              } catch (Exception e) {
                Initial.this.exceptionInternal(executor, e);
              }
            });
      }
    }
  }

  public static class Next<A, B> extends Async<B> {

    private final From<A, B> from;
    private final Async parent;

    public Next(Executor executor, From<A, B> from, Async parent) {
      super(executor);
      this.from = from;
      this.parent = parent;
    }

    public void remove() {
      //noinspection unchecked
      parent.remove(this);
    }

    private void onParentProgressInternal(int progress) {
      progressInternal(executor, from.onProgress(progress));
    }

    private void onParentResultInternal(final A result) {
      try {
        resultInternal(executor, from.onResult(result));
      } catch (Exception e) {
        exceptionInternal(executor, e);
      }
    }

    void onParentProgress(Executor parentExecutor, final int progress) {
      if (isCurrentExecutor(parentExecutor)) {
        onParentProgressInternal(progress);
      } else {
        this.executor.execute(() -> Next.this.onParentProgressInternal(progress));
      }
    }

    private boolean isCurrentExecutor(Executor executor) {
      return this.executor == INLINE || executor == this.executor;
    }

    void onParentResult(Executor parentExecutor, final A result) {
      if (isCurrentExecutor(parentExecutor)) {
        onParentResultInternal(result);
      } else {
        this.executor.execute(() -> Next.this.onParentResultInternal(result));
      }
    }

    void onParentException(Executor parentExecutor, final Exception exception) {
      if (isCurrentExecutor(parentExecutor)) {
        onParentExceptionInternal(exception);
      } else {
        this.executor.execute(() -> Next.this.onParentExceptionInternal(exception));
      }
    }

    private void onParentExceptionInternal(Exception exception) {
      try {
        Option<B> recovery = from.onException(exception);
        if (recovery.isPresent()) {
          resultInternal(executor, recovery.get());
        } else {
          exceptionInternal(executor, exception);
        }
      } catch (Exception e) {
        exceptionInternal(executor, e);
      }
    }
  }

  public abstract static class Result<A> extends From<A, A> {

    @Override
    public A onResult(A a) throws Exception {
      return a;
    }
  }

  public abstract static class From<A, B> implements OnResult<A, B>, OnException<B>, OnProgress {

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
