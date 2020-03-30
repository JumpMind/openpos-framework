package org.jumpmind.pos.core.flow;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jumpmind.util.AppUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class AsyncExecutorTest {

    AsyncExecutor asyncExecutor;

    @Before
    public void setup() {
        asyncExecutor = new AsyncExecutor();
        asyncExecutor.init();
    }

    @Test
    public void testExecuteSuccess() {
        final Object arg = new Object();
        final Object result = new Object();
        final MutableInt hit = new MutableInt(0);
        asyncExecutor.execute(arg, o -> {
            assertEquals(o, arg);
            hit.increment();
            return result;
        } , o -> {
            hit.increment();
            assertEquals(result, o);
        } , throwable -> {
            fail();
        });

        long ts = System.currentTimeMillis();
        while (hit.intValue() < 2 && System.currentTimeMillis()-ts < 5000) {
            AppUtils.sleep(50);
        }
        assertEquals(2, hit.intValue());
    }

    @Test
    public void testExecuteFailed() {
        final Object arg = new Object();
        final Object result = new Object();
        final MutableInt hit = new MutableInt(0);
        asyncExecutor.execute(arg, o -> {
            assertEquals(o, arg);
            hit.increment();
            throw new IllegalStateException();
        } , o -> { assertEquals(result, o);
        } , throwable -> {
            hit.increment();
            assertEquals(IllegalStateException.class, throwable.getClass());
        });

        long ts = System.currentTimeMillis();
        while (hit.intValue() < 2 && System.currentTimeMillis()-ts < 5000) {
            AppUtils.sleep(50);
        }
        assertEquals(2, hit.intValue());
    }
}
