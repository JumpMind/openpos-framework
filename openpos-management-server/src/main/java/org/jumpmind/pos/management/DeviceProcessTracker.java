package org.jumpmind.pos.management;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jumpmind.pos.util.model.ProcessInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Stores the current list of active Device Processes and their status.
 */
@Service
@Slf4j
public class DeviceProcessTracker {
    
    @Autowired
    OpenposManagementServerConfig config;
    
    @Autowired
    DeviceProcessStatusClient deviceProcessStatusClient;
    
    @Autowired
    private DeviceProcessLauncher processLauncher;
    
    
    Map<String, DeviceProcessInfo> trackingMap = new HashMap<>();
    Map<String, Boolean> lockMap = new HashMap<>();
    Map<String, DeviceProcessStatusKeeperThread> threadMap = new HashMap<>();

    /**
     * Allows a thread to request a lock for a given device so that other threads
     * will have access blocked to it. Intended to be used when a device process
     * is being initialized or started so that two or more threads cannot
     * simultaneously initialize or start it.  It the responsibility of the caller
     * to release the lock when done using {@link #unlock(String)}.
     * @param deviceId
     * @param maxWaitMills The maximum time to wait for the lock.  If <= 0,
     * this method will return immediately with a boolean value to indicate
     * whether or not the lock was acquired.
     * @return {@code true} if the lock was acquired, {@code false} if not.
     */
    public boolean waitForLock(String deviceId, long maxWaitMillis) {
        DeviceProcessLockWatcher watcher = null;
        synchronized(lockMap) {
            if (this.lock(deviceId)) {
                return true;
            } else if (maxWaitMillis > 0) {
                watcher = new DeviceProcessLockWatcher(deviceId);
                watcher.start();
            } else {
                return false;
            }
        }
        if (watcher != null) {
            try {
                synchronized(watcher) {
                    log.info("Waiting up to {}ms for Device Process '{}' lock", maxWaitMillis, deviceId);
                    watcher.wait(maxWaitMillis);
                    log.info("Lock status changed to unlocked or wait period expired for Device Process '{}'. lock status is: '{}'", deviceId, getDeviceLockStatus(deviceId));
                    return getDeviceLockStatus(deviceId);
                }
            } catch (InterruptedException ex) {
                log.warn("DeviceProcessLockWatcher interrupted", ex);
            } finally {
                watcher.stopRunning();
            }
        }
        
        return false;
    }
    
    /**
     * Begins tracking the given Device Process. A separate thread for the device
     * will be allocated to communicate with the remote Device Process in order
     * to maintain its status via polling.
     * @param dpi The Device Process to track.
     */
    public void track(DeviceProcessInfo dpi) {
        // intentionally using tracking map for synchronization for both threadMap 
        // and trackingMap since both maps should be in sync with the list of devices.
        synchronized (trackingMap) {  
            if (! threadMap.containsKey(dpi.getDeviceId())) {
                DeviceProcessStatusKeeperThread t = new DeviceProcessStatusKeeperThread(dpi.getDeviceId(), dpi.getPort());
                t.start();
                threadMap.put(dpi.getDeviceId(), t);
            }
        }
    }
    
    protected DeviceProcessInfo removeDeviceProcessInfo(DeviceProcessInfo pi) {
        synchronized(trackingMap) {
            DeviceProcessStatusKeeperThread statusThread = this.threadMap.remove(pi.getDeviceId());
            if (statusThread != null) {
                statusThread.requestStop();
            }
            return this.trackingMap.remove(pi.getDeviceId());
        }
    }
    
    public DeviceProcessInfo updateDeviceProcessStatus(String deviceId, DeviceProcessStatus newStatus) {
        synchronized(trackingMap) {
            DeviceProcessInfo dpi = trackingMap.get(deviceId);
            // protect against evicted devices
            if (dpi != null) {
                dpi.setStatus(newStatus);
            }
            return dpi;
        }        
    }

    public DeviceProcessInfo updateDeviceProcessStatus(String deviceId, DeviceProcessStatus newStatus, Integer pid) {
        synchronized(trackingMap) {
            DeviceProcessInfo dpi = trackingMap.get(deviceId);
            // protect against evicted devices
            if (dpi != null) {
                dpi.setStatus(newStatus);
                dpi.setPid(pid);
            }
            return dpi;
        }        
    }
    
    public DeviceProcessInfo updateDeviceProcessPort(String deviceId, int port) {
        synchronized(trackingMap) {
            DeviceProcessInfo dpi = trackingMap.get(deviceId);
            // protect against evicted devices
            if (dpi != null) {
                dpi.setPort(port);
            }
            return dpi;
        }        
    }

    public DeviceProcessInfo getDeviceProcessInfo(String deviceId) {
        return this.getDeviceProcessInfo(null, deviceId);
    }

    public DeviceProcessInfo getDeviceProcessInfo(String appId, String deviceId) {
        synchronized(trackingMap) {
            DeviceProcessInfo deviceProcessInfo = trackingMap.get(deviceId);
            if (deviceProcessInfo == null) {
                deviceProcessInfo = new DeviceProcessInfo(appId, deviceId, DeviceProcessStatus.NotRunning);
                trackingMap.put(deviceId, deviceProcessInfo);
            }

            return deviceProcessInfo;
        }        
    }
    
    public Boolean getDeviceLockStatus(String deviceId) {
        synchronized(lockMap) {
            return lockMap.get(deviceId);
        }        
    }
    
    protected boolean lock(String deviceId) {
        synchronized(lockMap) {
            Boolean lockStatus = this.getDeviceLockStatus(deviceId);
            if (lockStatus == null || ! lockStatus) {
                log.trace("Locked for {}", deviceId);
                this.lockMap.put(deviceId, Boolean.TRUE);
                return true;
            }
            
            return false;
        }        
    }
    
    public void unlock(String deviceId) {
        synchronized(lockMap) {
            Boolean lockStatus = this.getDeviceLockStatus(deviceId);
            if (lockStatus != null && lockStatus) {
                this.lockMap.put(deviceId, Boolean.FALSE);
                log.trace("Unlocked for {}", deviceId);
            }
        }        
    }
    
    public void clean() {
        synchronized (trackingMap) {
            trackingMap.clear();
            Iterator<Entry<String, DeviceProcessStatusKeeperThread>> threadIt = threadMap.entrySet().iterator();
            while(threadIt.hasNext()) {
                Entry<String, DeviceProcessStatusKeeperThread> entry = threadIt.next();
                entry.getValue().requestStop();
                threadIt.remove();
            }
        }
        synchronized (lockMap) {
            lockMap.clear();
        }
    }

    
    class DeviceProcessLockWatcher extends Thread {
        String deviceId;
        boolean stopped = false;
        
        DeviceProcessLockWatcher(String deviceId) {
            this.deviceId = deviceId;
        }
        
        public void stopRunning() {
            this.stopped = true;
        }
        
        @Override
        public void run() {
            while (! stopped) {
                Boolean deviceLocked = DeviceProcessTracker.this.lock(deviceId);
                if (deviceLocked) {
                    synchronized(this) {
                        this.notify();
                    }
                    return;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    log.warn("",ex);
                }
            }
        }
    }

    class DeviceProcessStatusKeeperThread extends Thread {
        
        private String deviceId;
        private int port;
        private boolean stopped = false;
        
        public DeviceProcessStatusKeeperThread(String deviceId, int port) {
            this.deviceId = deviceId;
            this.port = port;
        }
        
        public void requestStop() {
            this.stopped = true;
        }
        
        @Override
        public void run() {
            log.info("Status tracking thread for Device Process '{}' started", deviceId);
            while(! stopped) {
                DeviceProcessInfo dpi = getDeviceProcessInfo(deviceId);
                DeviceProcessStatus status = dpi.getStatus();
                log.trace("Running status check for Device Process '{}' on port {} (current status is: {})...", deviceId, port, dpi.getStatus());
                if (status == DeviceProcessStatus.StartupFailed
                    && dpi.getLastUpdated() != null 
                    && Instant.now().isAfter(dpi.getLastUpdated().plusMillis(config.getFailedStartupProcessRetentionPeriodMillis()))) {
                    try {
                        DeviceProcessTracker.this.processLauncher.kill(dpi, 0);
                    } catch (Exception ex) {
                        log.warn("Kill failed for Device Process '{}'. Reason: {}", dpi.getDeviceId(), ex.getMessage());
                    }
                    removeDeviceProcessInfo(dpi);
                    stopped = true;
                    log.info("Evicted dead Device Process '{}' having status '{}'", dpi.getDeviceId(), dpi.getStatus());
                } else if (status == DeviceProcessStatus.NotRunning
                    && dpi.getLastUpdated() != null 
                    && Instant.now().isAfter(dpi.getLastUpdated().plusMillis(config.getDeviceProcessConfig(dpi).getDeadProcessRetentionPeriodMillis()))) {
                    try {
                        processLauncher.kill(dpi, 0);
                    } catch (Exception ex) {
                        log.warn("Kill failed for Device Process '{}'. Reason: {}", dpi.getDeviceId(), ex.getMessage());
                    }
                    removeDeviceProcessInfo(dpi);
                    stopped = true;
                    log.info("Evicted dead Device Process '{}' having status '{}'", dpi.getDeviceId(), dpi.getStatus());
                } else {
                    checkAndUpdateDeviceProcessStatus(dpi, status);
                }
                
                if (! stopped) {
                    try {
                        sleep(config.getStatusCheckPeriodMillis());
                    } catch (InterruptedException e) {
                        log.warn("DeviceProcessStatusKeeperThread for Device Process '{}' interrupted!", deviceId);
                    }
                } else {
                    log.info("Status tracking thread for Device Process '{}' terminated", deviceId);
                }
            }
        }
        
        private void checkAndUpdateDeviceProcessStatus(DeviceProcessInfo dpi, DeviceProcessStatus currentStatus) {

            boolean running = false;
            try {
                ProcessInfo pi = DeviceProcessTracker.this.deviceProcessStatusClient.getRemoteProcessStatus(this.deviceId, this.port);
                if (pi != null) {
                    running = true;
                    // We're transitioning from Starting to Running
                    if (currentStatus == DeviceProcessStatus.Starting) {
                        // We don't need to capture output any longer since the
                        // process is now fully started.
                        if (dpi.getStreamCopier() != null) {
                            log.debug("Stopping output to process log file for Device Process {}", deviceId);
                            dpi.getStreamCopier().stopCopying();
                        } else {
                            log.trace("StreamCopier is null for {}", deviceId);
                        }
                        log.info("Device Process '{}' status changed from {} to {}", dpi.getDeviceId(), DeviceProcessStatus.Starting,
                                DeviceProcessStatus.Running);
                    } else if (currentStatus == DeviceProcessStatus.NotRunning) {
                        log.info("Device Process '{}' status changed from {} to {}", dpi.getDeviceId(), DeviceProcessStatus.NotRunning,
                                DeviceProcessStatus.Running);
                    }
                    if (pi.getPid() != null) {
                        if (dpi.getPid() == null) {
                            log.info("Device Process '{}' pid is: {}", this.deviceId, pi.getPid());
                        }
                        updateDeviceProcessStatus(this.deviceId, DeviceProcessStatus.Running, pi.getPid());
                    } else {
                        updateDeviceProcessStatus(this.deviceId, DeviceProcessStatus.Running);
                    }
                }
            } catch (Exception ex) {
                log.trace("Failed to get status for Device Process '{}'.  Reason: {}", deviceId, ex.getMessage());
                running = false;
            }

            if (!running) {
                if (currentStatus == DeviceProcessStatus.Starting) {
                    log.trace("Device Process '{}' is 'Starting', will check again soon...", deviceId);
                } else if (currentStatus == DeviceProcessStatus.Running) {
                    updateDeviceProcessStatus(this.deviceId, DeviceProcessStatus.NotRunning);
                    log.info("Device Process '{}' status changed from {} to {}", deviceId, DeviceProcessStatus.Running,
                            DeviceProcessStatus.NotRunning);
                }
            }
        }
    }
    
}
