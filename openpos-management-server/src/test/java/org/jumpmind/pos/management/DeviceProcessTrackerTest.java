package org.jumpmind.pos.management;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "localtest")
@EnableScheduling
public class DeviceProcessTrackerTest {
    static final String DEVICE_ID_1 = "00000-001";
    static final String DEVICE_ID_2 = "00000-002";

    @Autowired
    DeviceProcessTracker tracker;
    
    @Mock
    DeviceProcessStatusClient statusClient;
  
    @Autowired
    OpenposManagementServerConfig config;
    
    long deviceProcessStatusCheckPeriodMillis;
    
    
    @Before
    public void setUp() throws Exception {
        deviceProcessStatusCheckPeriodMillis = config.getStatusCheckPeriodMillis();
        tracker.deviceProcessStatusClient = statusClient;
        tracker.clean();
    }


    /**
     * Should be no waiting since there is only one thread running.
     */
    @Test(timeout=500)
    public void testLockWait_SingleThread() {
        System.out.println("testLockWait_SingleThread");
        assertThat(tracker.getDeviceLockStatus(DEVICE_ID_1)).isNull();
        tracker.waitForLock(DEVICE_ID_1, 10000);
        assertThat(tracker.getDeviceLockStatus(DEVICE_ID_1)).isEqualTo(Boolean.TRUE);
    }
    
    /**
     * First thread will initiate lock, second thread will wait until given timeout.
     */
    @Test(timeout=2000)
    public void testWaitOnLock_TwoThreadsSameDevice() throws InterruptedException {
        System.out.println("testWaitOnLock_TwoThreadsSameDevice");
        
        TestProcessLockThread t2 = new TestProcessLockThread();
        assertNull(tracker.getDeviceLockStatus(DEVICE_ID_1));
        tracker.waitForLock(DEVICE_ID_1, 10000);
        assertTrue(tracker.getDeviceLockStatus(DEVICE_ID_1));
        t2.start();
        synchronized(t2) {
            if (!t2.finished.get()) {
                t2.wait();
            }
        }
        assertThat(t2.elapsedTimeMillis).isGreaterThanOrEqualTo(1500).isLessThan(2000);
    }
        

    /**
     * There should be no waiting period for each thread since each thread is getting a lock for
     * a difference device process.
     */
    @Test(timeout=600)
    public void testWaitForLock_TwoThreadsDifferentDevices() throws InterruptedException {
        System.out.println("testWaitForLock_TwoThreadsDifferentDevices");
        TestProcessLockThread t1 = new TestProcessLockThread(DEVICE_ID_1, 1500);
        assertNull(tracker.getDeviceLockStatus(DEVICE_ID_1));
        
        TestProcessLockThread t2 = new TestProcessLockThread(DEVICE_ID_2, 1500);
        assertNull(tracker.getDeviceLockStatus(DEVICE_ID_2));
        t1.start();
        t2.start();
        
        synchronized(t1) {
            if (! t1.finished.get()) {
                t1.wait();
            }
        }
        
        synchronized(t2) {
            if (! t2.finished.get()) {
                t2.wait();
            }
        }
        
        assertThat(tracker.getDeviceLockStatus(DEVICE_ID_1)).isTrue();
        assertThat(t1.elapsedTimeMillis).isGreaterThanOrEqualTo(0).isLessThan(500);
        
        assertThat(tracker.getDeviceLockStatus(DEVICE_ID_2)).isTrue();
        assertThat(t2.elapsedTimeMillis).isGreaterThanOrEqualTo(0).isLessThan(500);
        
    }
    
    /**
     * Confirms that web service call is made by the scheduler once there is a port
     * number established for a Device Process
     * @throws InterruptedException
     */
    @Test
    public void testCheckDeviceProcessStatus_SingleDevice() throws InterruptedException {
        System.out.println("testCheckDeviceProcessStatus_SingleDevice");
        DeviceProcessInfo pi = tracker.getDeviceProcessInfo(DEVICE_ID_1);
        tracker.updateDeviceProcessPort(DEVICE_ID_1, 9999);
        tracker.track(pi);

        // Doing this instead of using sleep in order to save test execution time
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        final AtomicBoolean verified = new AtomicBoolean(false);
        Runnable v = () -> {
            try {
                if (! verified.get()) {
                    verify(statusClient, atLeastOnce()).getRemoteProcessStatus(DEVICE_ID_1, 9999);
                    verified.set(true);
                }
            } catch (Throwable ex) {
                // Keep waiting
            }
        };
        
        Future<?> verifier = executor.scheduleAtFixedRate(v, 0, 100, TimeUnit.MILLISECONDS);
        Runnable cv = () -> {
            if (verified.get()) {
                verifier.cancel(true); 
                executor.shutdown();
            }
        };
        Future<?> checkVerifier = executor.scheduleAtFixedRate(cv, 0, 110, TimeUnit.MILLISECONDS);

        
        executor.awaitTermination(this.deviceProcessStatusCheckPeriodMillis * 2, TimeUnit.MILLISECONDS);
        assertTrue("Expected getDeviceProcessStatus to be called at least once", verified.get());
    }
    
    /**
     * Confirms that web service calls are made by the scheduler once there is a port
     * number established for each of the Device Processes
     * @throws InterruptedException
     */
    @Test
    public void testCheckDeviceProcessStatus_MultipleDevices() throws InterruptedException {
        System.out.println("testCheckDeviceProcessStatus_MultipleDevices");
        DeviceProcessInfo pi1 = tracker.getDeviceProcessInfo(DEVICE_ID_1);
        DeviceProcessInfo pi2 = tracker.getDeviceProcessInfo(DEVICE_ID_2);
        tracker.updateDeviceProcessPort(DEVICE_ID_1, 8888);
        tracker.updateDeviceProcessPort(DEVICE_ID_2, 9999);
        tracker.track(pi1);
        tracker.track(pi2);
        
        // Doing this instead of using sleep in order to save test execution time
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        final AtomicBoolean verified = new AtomicBoolean(false);
        Runnable v = () -> {
            try {
                if (! verified.get()) {
                    verify(statusClient, atLeastOnce()).getRemoteProcessStatus(DEVICE_ID_1, 8888);
                    verify(statusClient, atLeastOnce()).getRemoteProcessStatus(DEVICE_ID_2, 9999);
                    verified.set(true);
                }
            } catch (Throwable ex) {
                // Keep waiting
            }
        };
        
        Future<?> verifier = executor.scheduleAtFixedRate(v, 0, 100, TimeUnit.MILLISECONDS);
        Runnable cv = () -> {
            if (verified.get()) {
                verifier.cancel(true); 
                executor.shutdown();
            }
        };
        Future<?> checkVerifier = executor.scheduleAtFixedRate(cv, 0, 110, TimeUnit.MILLISECONDS);

        
        executor.awaitTermination(this.deviceProcessStatusCheckPeriodMillis * 2, TimeUnit.MILLISECONDS);
        assertTrue("Expected getDeviceProcessStatus to be called at least once for both devices", verified.get());
    }

    
    class TestProcessLockThread extends Thread {
        public long startMillis;
        public long endMillis;
        public long elapsedTimeMillis;
        public AtomicBoolean finished = new AtomicBoolean(false);
        
        public String deviceId = DEVICE_ID_1;
        public long maxWaitIfStartingMillis = 1500;
        
        public TestProcessLockThread() {
        }
        public TestProcessLockThread(String deviceId) {
            this.deviceId = deviceId;
        }
        public TestProcessLockThread(String deviceId, long maxWait) {
            this.deviceId = deviceId;
            this.maxWaitIfStartingMillis = maxWait;
        }
        
        public void run() {
            startMillis = System.currentTimeMillis();
            DeviceProcessTrackerTest.this.tracker.waitForLock(this.deviceId, this.maxWaitIfStartingMillis);
            endMillis = System.currentTimeMillis();
            elapsedTimeMillis = endMillis - startMillis;
            synchronized(this) {
                finished.set(true);
                notify();
            }
        }
    }
    
}
