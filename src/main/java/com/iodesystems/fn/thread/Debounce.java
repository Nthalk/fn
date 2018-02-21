package com.iodesystems.fn.thread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Debounce implements Runnable {
    static final Clock DEFAULT_CLOCK = new Clock();
    private static final Logger LOG = Logger.getLogger(Debounce.class.getName());
    private final ScheduledExecutorService scheduledExecutorService;
    private final long millisPerTrigger;
    private final boolean leading;
    private final boolean continuing;
    private final boolean trailing;
    Clock clock = DEFAULT_CLOCK;
    long lastTriggered;
    private boolean isLeading = true;
    private ScheduledFuture<?> next = null;

    public Debounce(long millisPerTrigger, boolean leading, boolean continuing) {
        this(null, millisPerTrigger, leading, continuing, false);
    }

    public Debounce(ScheduledExecutorService scheduledExecutorService, long millisPerTrigger, boolean leading, boolean continuing, boolean trailing) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.millisPerTrigger = millisPerTrigger;
        this.leading = leading;
        this.continuing = continuing;
        this.trailing = trailing;
        lastTriggered = clock.currentTimeMillis();
    }

    public static Debounce debounceWithDelay(long millisPerFire, final Runnable runnable) {
        return new Debounce(millisPerFire, false, false) {
            @Override
            public void trigger() {
                runnable.run();
            }
        };
    }

    public static Debounce debounce(long millisPerFire, final Runnable runnable) {
        return new Debounce(millisPerFire, true, false) {
            @Override
            public void trigger() {
                runnable.run();
            }
        };
    }

    public static Debounce debounceWithDelay(ScheduledExecutorService scheduledExecutorService, long millisPerFire, final Runnable runnable) {
        return new Debounce(scheduledExecutorService, millisPerFire, false, false, true) {
            @Override
            public void trigger() {
                runnable.run();
            }
        };
    }

    public static Debounce debounce(ScheduledExecutorService scheduledExecutorService, long millisPerFire, final Runnable runnable) {
        return new Debounce(scheduledExecutorService, millisPerFire, true, false, true) {
            @Override
            public void trigger() {
                runnable.run();
            }
        };
    }

    public static Debounce throttleWithDelay(ScheduledExecutorService scheduledExecutorService, long millisPerFire, final Runnable runnable) {
        return new Debounce(scheduledExecutorService, millisPerFire, false, true, true) {
            @Override
            public void trigger() {
                runnable.run();
            }
        };
    }

    public static Debounce throttle(ScheduledExecutorService scheduledExecutorService, long millisPerFire, final Runnable runnable) {
        return new Debounce(scheduledExecutorService, millisPerFire, true, true, true) {
            @Override
            public void trigger() {
                runnable.run();
            }
        };
    }

    public static Debounce throttleWithDelay(long millisPerFire, final Runnable runnable) {
        return new Debounce(millisPerFire, false, true) {
            @Override
            public void trigger() {
                runnable.run();
            }
        };
    }

    public static Debounce throttle(long millisPerFire, final Runnable runnable) {
        return new Debounce(millisPerFire, true, true) {
            @Override
            public void trigger() {
                runnable.run();
            }
        };
    }

    public void cancel() {
        synchronized (this) {
            if (next != null) {
                next.cancel(true);
            }
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            LOG.log(Level.FINEST, "Trigger attempted");
            long current = clock.currentTimeMillis();
            long delta = current - lastTriggered;

            if (next != null && next.cancel(false)) {
                LOG.log(Level.FINEST, "Clearing deferred trailing trigger");
                next = null;
            }
            if (leading && isLeading) {
                LOG.log(Level.FINEST, "Triggered due to leading invocation");
                trigger();
                lastTriggered = current;
                isLeading = false;
            } else if (delta >= millisPerTrigger) {
                LOG.log(Level.FINEST, "Triggered due to acceptable delay");
                trigger();
                lastTriggered = current;
            } else {
                if (!continuing) {
                    LOG.log(Level.FINEST, "Resetting lastTriggered due to non-continuing");
                    lastTriggered = current;
                }
                if (trailing) {
                    LOG.log(Level.FINEST, "Scheduling deferred trailing trigger");
                    next = scheduledExecutorService.schedule(this, millisPerTrigger, TimeUnit.MILLISECONDS);
                }

            }
        }
    }

    abstract void trigger();

    static class Clock {
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }
}
