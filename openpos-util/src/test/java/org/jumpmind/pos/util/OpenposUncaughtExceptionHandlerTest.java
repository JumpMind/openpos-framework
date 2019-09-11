package org.jumpmind.pos.util;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class OpenposUncaughtExceptionHandlerTest {

    OpenposUncaughtExceptionHandler globalExHandler;
    OpenposUncaughtExceptionHandler globalExHandlerSpy;
    Logger loggerSpy;
    
    @Mock
    private Logger mockLogger;
    
    @After
    public void tearDown() {
        Thread.setDefaultUncaughtExceptionHandler(null);
    }
    
    @Before
    public void setUp() throws Exception {
        globalExHandler = new OpenposUncaughtExceptionHandler();
        globalExHandlerSpy = spy(globalExHandler);
        when(globalExHandlerSpy.logger()).thenReturn(mockLogger);
        Thread.setDefaultUncaughtExceptionHandler(globalExHandlerSpy);
    }

    /**
     * Ensure that a single thread, which uses the defaultUncaughtException handler set on 
     * the Thread class, has the handler's uncaughtException method invoked when
     * the thread raises an unhandled exception.
     * @throws InterruptedException
     */
    @Test
    public void testSingleThreadWithDefaultUncaughtExHandler() throws InterruptedException {
        final RuntimeException ex1 = new RuntimeException("Exception 1");
        Thread t1 = new Thread(() -> {throw  ex1;});
        t1.start();
        t1.join();
        verify(globalExHandlerSpy, times(1)).uncaughtException(eq(t1), eq(ex1));
        verify(mockLogger, times(1)).error(anyString(), eq(ex1));
    }


    /**
     * Ensure that two threads, which both use the defaultUncaughtException handler set on 
     * the Thread class, have the handler's uncaughtException method invoked for each thread when
     * each thread raises an unhandled exception.
     * @throws InterruptedException
     */
    @Test
    public void testMultipleThreadsWithDefaultUncaughtExHandler() throws InterruptedException {
        final RuntimeException ex1 = new RuntimeException("Exception 1");
        final RuntimeException ex2 = new RuntimeException("Exception 2");
        Thread t1 = new Thread(() -> {throw  ex1;});
        Thread t2 = new Thread(() -> {throw  ex2;});
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        verify(globalExHandlerSpy, times(2)).uncaughtException(any(), any());
        verify(globalExHandlerSpy, times(1)).uncaughtException(eq(t1), eq(ex1));
        verify(globalExHandlerSpy, times(1)).uncaughtException(eq(t2), eq(ex2));
        verify(mockLogger, times(1)).error(anyString(), eq(ex1));
        verify(mockLogger, times(1)).error(anyString(), eq(ex2));
    }

    /**
     * Ensure that two threads, which both use the same instance level uncaught exception handler set on 
     * them, have the handler's uncaughtException method invoked for each thread when
     * each thread raises an unhandled exception.  Also ensure the global uncaught exception handler 
     * is not invoked.
     * @throws InterruptedException
     */
    @Test
    public void testMultipleThreadsWithSameUncaughtExHandler() throws InterruptedException {
        final RuntimeException ex1 = new RuntimeException("Exception 1");
        final RuntimeException ex2 = new RuntimeException("Exception 2");
        OpenposUncaughtExceptionHandler threadExHandler = new OpenposUncaughtExceptionHandler();
        OpenposUncaughtExceptionHandler threadExHandlerSpy = spy(threadExHandler);
        when(threadExHandlerSpy.logger()).thenReturn(mockLogger);

        Thread t1 = new Thread(() -> {throw  ex1;});
        t1.setUncaughtExceptionHandler(threadExHandlerSpy);
        Thread t2 = new Thread(() -> {throw  ex2;});
        t2.setUncaughtExceptionHandler(threadExHandlerSpy);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        verify(globalExHandlerSpy, times(0)).uncaughtException(any(), any());
        verify(threadExHandlerSpy, times(2)).uncaughtException(any(), any());
        verify(threadExHandlerSpy, times(1)).uncaughtException(eq(t1), eq(ex1));
        verify(threadExHandlerSpy, times(1)).uncaughtException(eq(t2), eq(ex2));
        verify(mockLogger, times(1)).error(anyString(), eq(ex1));
        verify(mockLogger, times(1)).error(anyString(), eq(ex2));
    }

    /**
     * Ensure that two threads, which both use the different instance level uncaught exception handler set on 
     * them, have their respective handler's uncaughtException method invoked when
     * each thread raises an unhandled exception. Also ensure the global uncaught exception handler 
     * is not invoked.
     * @throws InterruptedException
     */
    @Test
    public void testMultipleThreadsWithDifferentUncaughtExHandler() throws InterruptedException {
        final RuntimeException ex1 = new RuntimeException("Exception 1");
        final RuntimeException ex2 = new RuntimeException("Exception 2");
        OpenposUncaughtExceptionHandler threadExHandler1 = new OpenposUncaughtExceptionHandler();
        OpenposUncaughtExceptionHandler threadExHandlerSpy1 = spy(threadExHandler1);
        OpenposUncaughtExceptionHandler threadExHandler2 = new OpenposUncaughtExceptionHandler();
        OpenposUncaughtExceptionHandler threadExHandlerSpy2 = spy(threadExHandler2);
        when(threadExHandlerSpy1.logger()).thenReturn(mockLogger);
        when(threadExHandlerSpy2.logger()).thenReturn(mockLogger);

        Thread t1 = new Thread(() -> {throw  ex1;});
        t1.setUncaughtExceptionHandler(threadExHandlerSpy1);
        Thread t2 = new Thread(() -> {throw  ex2;});
        t2.setUncaughtExceptionHandler(threadExHandlerSpy2);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        verify(globalExHandlerSpy, times(0)).uncaughtException(any(), any());
        verify(threadExHandlerSpy1, times(1)).uncaughtException(eq(t1), eq(ex1));
        verify(threadExHandlerSpy2, times(1)).uncaughtException(eq(t2), eq(ex2));
        verify(mockLogger, times(1)).error(anyString(), eq(ex1));
        verify(mockLogger, times(1)).error(anyString(), eq(ex2));
    }

    /**
     * Ensure that two threads, where one thread relies on its own uncaught exception 
     * handler and the other thread relies on the default uncaught exception handler, have 
     * their correct handler's uncaughtException method invoked when
     * each thread raises an unhandled exception. Also ensure the global uncaught exception handler 
     * is only invoked one time.
     * @throws InterruptedException
     */
    @Test
    public void testMultipleThreadsWithDefaultAndNonDefaultUncaughtExHandler() throws InterruptedException {
        final RuntimeException ex1 = new RuntimeException("Exception 1");
        final RuntimeException ex2 = new RuntimeException("Exception 2");
        OpenposUncaughtExceptionHandler threadExHandler1 = new OpenposUncaughtExceptionHandler();
        OpenposUncaughtExceptionHandler threadExHandlerSpy1 = spy(threadExHandler1);
        when(threadExHandlerSpy1.logger()).thenReturn(mockLogger);

        Thread t1 = new Thread(() -> {throw  ex1;});
        t1.setUncaughtExceptionHandler(threadExHandlerSpy1);
        Thread t2 = new Thread(() -> {throw  ex2;});  // Will use default handler
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        verify(globalExHandlerSpy, times(1)).uncaughtException(any(), any());
        verify(threadExHandlerSpy1, times(1)).uncaughtException(eq(t1), eq(ex1));
        verify(globalExHandlerSpy, times(1)).uncaughtException(eq(t2), eq(ex2));
        verify(mockLogger, times(1)).error(anyString(), eq(ex1));
        verify(mockLogger, times(1)).error(anyString(), eq(ex2));
    }
    
}
