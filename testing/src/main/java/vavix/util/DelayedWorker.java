package vavix.util;

/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * DelayedWorker.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/02/13 umjammer initial version <br>
 */
public final class DelayedWorker {

    private DelayedWorker() {
    }

    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * @param millis delay in milliseconds
     */
    public static void later(long millis, Runnable r) {
        scheduler.schedule(r, millis, TimeUnit.MILLISECONDS);
    }

    @FunctionalInterface
    public interface DelayedWorkDetector {
        /**
         * delayed scheduled time to come or not
         */
        boolean come();
    }

    private static ThreadLocal<DelayedWorkDetector> detectors = new ThreadLocal<>();

    /**
     * @param millis delay in milliseconds
     */
    public static DelayedWorkDetector later(long millis) {
        DelayedWorkDetector detector = detectors.get();
        if (detector == null) {
            detector = new DelayedWorkDetector() {
                boolean flag = false;
                boolean exec = false;

                public boolean come() {
                    if (!exec) {
                        later(millis, this::exec);
                        exec = true;
                    }
                    if (flag) {
                        detectors.remove();
                    }
                    return flag;
                }

                private void exec() {
                    flag = true;
                }
            };
            detectors.set(detector);
        }
        return detector;
    }
}
