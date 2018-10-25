package com.iodesystems.fn.thread;

import com.iodesystems.fn.data.From;
import com.iodesystems.fn.logic.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class PipedProcessNode<IN, OUT> implements Handler<IN> {
  private final From<IN, OUT> processor;
  private final Executor executor;
  private final List<Handler<OUT>> downstream = new ArrayList<>();

  public PipedProcessNode(Executor executor, From<IN, OUT> processor) {
    this.executor = executor;
    this.processor = processor;
  }

  public void submit(IN data) {
    OUT processed = processor.from(data);
    for (Handler<OUT> handler : downstream) {
      handler.handle(processed);
    }
  }

  @Override
  public void handle(IN data) {
    if (executor != null) {
      executor.execute(() -> submit(data));
    } else {
      submit(data);
    }
  }

  public void downstream(Handler<OUT> handler) {
    downstream.add(handler);
  }
}
