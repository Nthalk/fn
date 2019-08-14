package com.iodesystems.fn.thread;

import com.iodesystems.fn.Fl;
import com.iodesystems.fn.data.FlakeyFrom;
import com.iodesystems.fn.data.From;
import com.iodesystems.fn.data.From2;
import com.iodesystems.fn.logic.FlakeyHandler;
import com.iodesystems.fn.logic.Handler;
import com.iodesystems.fn.logic.Where;
import java.util.concurrent.Executor;

public class PipedProcessNodeBuilder<SOURCE, IN, OUT> {

  private Fl<SOURCE> source;
  private PipedProcessNode<IN, OUT> tip;
  private Executor executor;
  private Where<OUT> guard = null;

  public PipedProcessNodeBuilder(Fl<SOURCE> source, PipedProcessNode<IN, OUT> tip) {
    this.source = source;
    this.tip = tip;
  }

  public PipedProcessNodeBuilder<SOURCE, IN, OUT> fork(
      Handler<PipedProcessNodeBuilder<SOURCE, IN, OUT>> fork) {
    fork.handle(this);
    return this;
  }

  public PipedProcessNodeBuilder<SOURCE, IN, OUT> when(Where<OUT> guard) {
    this.guard = guard;
    return this;
  }

  public PipedProcessNodeBuilder<SOURCE, IN, OUT> on(Executor executor) {
    this.executor = executor;
    return this;
  }

  public PipedProcessNodeBuilder<SOURCE, IN, OUT> then(
      FlakeyHandler<OUT> handler, From2<OUT, Exception, OUT> error) {
    return then(
        d -> {
          try {
            handler.handle(d);
          } catch (Exception e) {
            error.from(d, e);
          }
        });
  }

  public PipedProcessNodeBuilder<SOURCE, IN, OUT> then(Handler<OUT> handler) {
    PipedProcessNode<OUT, OUT> processor =
        new PipedProcessNode<>(
            executor,
            o -> {
              handler.handle(o);
              return o;
            });
    tip.downstream(guarded(processor));
    return this;
  }

  public <NEXT> PipedProcessNodeBuilder<SOURCE, OUT, NEXT> convert(
      FlakeyFrom<OUT, NEXT> convert, From2<OUT, Exception, NEXT> error) {
    return convert(
        d -> {
          try {
            return convert.from(d);
          } catch (Exception e) {
            return error.from(d, e);
          }
        });
  }

  public <NEXT> PipedProcessNodeBuilder<SOURCE, OUT, NEXT> convert(From<OUT, NEXT> convert) {
    PipedProcessNode<OUT, NEXT> processor = new PipedProcessNode<>(executor, convert);
    tip.downstream(guarded(processor));
    return new PipedProcessNodeBuilder<>(source, processor);
  }

  private Handler<OUT> guarded(Handler<OUT> handler) {
    if (guard != null) {
      final Where<OUT> guardFinal = guard;
      guard = null;
      return o -> {
        if (guardFinal.is(o)) {
          handler.handle(o);
        }
      };
    } else {
      return handler;
    }
  }

  public Fl<SOURCE> build() {
    return source;
  }
}
