package org.jumpmind.pos.core.flow;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jumpmind.util.AppUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class AsyncExecutorTest {

    AsyncExecutor asyncExecutor;

    @Before
    public void setup() {
        asyncExecutor = new AsyncExecutor();
        asyncExecutor.stateManagerContainer = new StateManagerContainer();
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

    @Test
    public void testExecuteCancelled() {
        final Object arg = new Object();
        final MutableInt result = new MutableInt();
        final MutableInt hit = new MutableInt(0);
        final MutableInt beforeCancel = new MutableInt(0);

        asyncExecutor.execute(arg, o -> {
            assertEquals(o, arg);
            AppUtils.sleep(200);
            result.increment();
            return result;
        } , o -> {
            hit.increment();
        } , throwable -> {
            hit.increment();
        }, o -> {
            assertEquals(result, o);
        }, ()->{ beforeCancel.increment();});
        asyncExecutor.cancel();

        while (result.intValue() == 0) {
            AppUtils.sleep(100);
        }
        assertEquals(1, beforeCancel.intValue());
        assertEquals(0, hit.intValue());
    }
}
