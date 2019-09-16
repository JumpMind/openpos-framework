package org.jumpmind.pos.util;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An instance of this class can be passed to either the {@link java.lang.Thread#setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler) setDefaultUncaughtExceptionHandler} method
 * for globally logging all unhandled exceptions on all Threads or can be passed to an individual Thread's {@link java.lang.Thread#setUncaughtExceptionHandler(UncaughtExceptionHandler) setUncaughtExceptionHandler}
 * method for logging uncaught exceptions on an individual thread.
 *
 */
public class OpenposUncaughtExceptionHandler implements UncaughtExceptionHandler {
    final protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger().error(String.format("An uncaught exception was raised from thread %s. Stack trace follows.", t), e);
    }
    
    Logger logger() {
        return logger;
    }

}
