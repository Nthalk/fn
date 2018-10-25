package com.iodesystems.fn.thread;

import java.util.concurrent.Executor;

public class Deferred<A> extends Async<A> {

  public Deferred(Executor executor) {
    super(executor);
  }

  @Override
  protected synchronized <B> Next<A, B> then(Next<A, B> next) {
    if (progress >= 0) {
      next.onParentProgress(null, progress);
    }
    if (result != null) {
      next.onParentResult(null, result.orElse(null));
    } else if (exception != null) {
      next.onParentException(null, exception);
    }
    nexts.add(next);
    return next;
  }

  public void progress(int progress) {
    if (progress > -1) {
      progressInternal(null, progress);
    }
  }

  public void result(A result) {
    resultInternal(null, result);
  }

  public void exception(Exception exception) {
    exceptionInternal(null, exception);
  }
}
