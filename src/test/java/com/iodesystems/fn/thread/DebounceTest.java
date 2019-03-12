package com.iodesystems.fn.thread;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;

public class DebounceTest {

  private TestClock clock;
  private ScheduledExecutorService scheduledExecutorService;
  private AtomicInteger count;

  @Before
  public void setUp() {
    clock = new TestClock();
    scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    count = new AtomicInteger();
  }

  @Test
  public void testThrottleWithDelay() throws InterruptedException {
    Debounce debounce =
        Debounce.throttleWithDelay(scheduledExecutorService, 2, () -> count.incrementAndGet());
    debounce.clock = clock;
    debounce.lastTriggered = clock.currentTimeMillis();

    debounce.run();
    assertEquals(0, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(0, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(1, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(1, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(2, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(2, count.get());

    clock.advance(10);
    Thread.sleep(10);
    assertEquals(3, count.get());
  }

  @Test
  public void testThrottleWithDelayWithoutTrailing() throws InterruptedException {
    Debounce debounce = Debounce.throttleWithDelay(2, () -> count.incrementAndGet());
    debounce.clock = clock;
    debounce.lastTriggered = clock.currentTimeMillis();

    // Delay prevents it from running
    debounce.run();
    assertEquals(0, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(0, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(1, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(1, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(2, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(2, count.get());

    clock.advance(10);
    Thread.sleep(10);
    assertEquals(2, count.get());
  }

  @Test
  public void testDebounceWithDelayWithoutTrailing() throws InterruptedException {
    Debounce debounce = Debounce.debounceWithDelay(1, () -> count.incrementAndGet());
    debounce.clock = clock;
    // Delay prevents it from running
    debounce.run();
    assertEquals(0, count.get());
    debounce.run();
    assertEquals(0, count.get());
    debounce.run();
    assertEquals(0, count.get());

    clock.advance(2);
    debounce.run();
    assertEquals(1, count.get());

    clock.advance(2);
    debounce.run();
    assertEquals(2, count.get());

    debounce.run();
    assertEquals(2, count.get());

    clock.advance(10);
    // Should not run trailing here
    Thread.sleep(10);

    assertEquals(2, count.get());
  }

  @Test
  public void testDebounceWithDelay() throws Exception {
    Debounce debounce =
        Debounce.debounceWithDelay(scheduledExecutorService, 1, () -> count.incrementAndGet());
    debounce.clock = clock;
    // Delay prevents it from running
    debounce.run();
    assertEquals(0, count.get());
    debounce.run();
    assertEquals(0, count.get());
    debounce.run();
    assertEquals(0, count.get());

    clock.advance(2);
    debounce.run();
    assertEquals(1, count.get());

    clock.advance(2);
    debounce.run();
    assertEquals(2, count.get());

    debounce.run();
    assertEquals(2, count.get());

    clock.advance(10);
    // Should run trailing here
    Thread.sleep(10);

    assertEquals(3, count.get());
  }

  @Test
  public void testDebounceInitial() throws Exception {
    Debounce debounce =
        Debounce.debounce(scheduledExecutorService, 1, () -> count.incrementAndGet());
    debounce.clock = clock;
    debounce.run();
    // Should run here
    clock.advance(10);
    Thread.sleep(10);
    assertEquals(1, count.get());
  }

  @Test
  public void testDebounceWithoutTrailing() throws Exception {
    Debounce debounce = Debounce.debounce(1, () -> count.incrementAndGet());
    debounce.clock = clock;
    debounce.run();
    assertEquals(1, count.get());

    debounce.run();
    debounce.run();
    debounce.run();
    assertEquals(1, count.get());

    clock.advance(10);
    debounce.run();
    assertEquals(2, count.get());

    clock.advance(10);
    Thread.sleep(10);
    // Should skip this

    assertEquals(2, count.get());
  }

  @Test
  public void testThrottle() throws Exception {
    Debounce debounce = Debounce.throttle(2, () -> count.incrementAndGet());
    debounce.clock = clock;

    debounce.run();
    assertEquals(1, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(1, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(2, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(2, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(3, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(3, count.get());

    clock.advance(10);
    Thread.sleep(10);
    assertEquals(3, count.get());
  }

  @Test
  public void testThrottleWithTrailing() throws Exception {
    Debounce debounce =
        Debounce.throttle(scheduledExecutorService, 2, () -> count.incrementAndGet());
    debounce.clock = clock;

    debounce.run();
    assertEquals(1, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(1, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(2, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(2, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(3, count.get());

    clock.advance(1);
    debounce.run();
    assertEquals(3, count.get());

    clock.advance(10);
    Thread.sleep(10);
    assertEquals(4, count.get());
  }

  static class TestClock extends Debounce.Clock {

    public long currentTime;

    @Override
    public long currentTimeMillis() {
      return currentTime;
    }

    public void advance(long milliseconds) {
      currentTime += milliseconds;
    }
  }
}
