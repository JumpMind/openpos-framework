package org.jumpmind.pos.management;


import java.time.Duration;
import java.time.Instant;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.util.NetworkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessManagerService {
    @Autowired
    OpenposManagementServerConfig config;

    @Autowired
    private ProcessManagerEnvironmentService processMgrEnvSvc;
    
    @Autowired
    private DeviceProcessTracker processTracker;
    
    @Autowired
    private DeviceProcessLauncher processLauncher;
    

    @PostConstruct
    protected void init() {
        processMgrEnvSvc.ensureMainWorkDirExists();
    }
    
    public DeviceProcessInfo queryOrLaunchDeviceProcess(String appId, String deviceId) {
        // If it's running just return immediately, otherwise start it up or wait
        // to see if it comes up
        DeviceProcessInfo pi = processTracker.getDeviceProcessInfo(appId, deviceId);
        if (pi.getStatus() == DeviceProcessStatus.Running) {
            return pi;
        }
        boolean gotLock = false;    
        try {
            if (processTracker.waitForLock(deviceId, 0)) {
                gotLock = true;
            } else {
                log.trace("Another request already has a lock for Device Process '{}', try again later", deviceId);
                return null;
            }
            
            long processStartMaxWaitMillis = config.getDeviceProcessConfig(appId).getStartMaxWaitMillis();
            Instant start = Instant.now();
            
            while (Duration.between(start, Instant.now()).toMillis() < processStartMaxWaitMillis) {
                long timeRemaining = processStartMaxWaitMillis - Duration.between(start, Instant.now()).toMillis();
                boolean takeNap = false;
                pi = processTracker.getDeviceProcessInfo(appId, deviceId);
                if (pi.getStatus() == DeviceProcessStatus.Running) {
                    return pi;
                } else if (pi.getStatus() == DeviceProcessStatus.NotRunning) {
                    try {
                        if (pi.isNotPreviouslyStarted()) {
                            pi = this.processLauncher.launch(pi, timeRemaining);
                            if (pi.isProcessAlive()) {
                                processTracker.track(pi);
                                processTracker.updateDeviceProcessStatus(deviceId, DeviceProcessStatus.Starting);
                            } else {
                                throw new DeviceProcessLaunchException(
                                        String.format("Failed to create process for Device '%s'.  Check the process log for the additional info.", pi.getDeviceId())
                                );
                            }
                        } else {
                            log.info("Device Process '{}' is NotRunning, will check it again in a second. Time remaining: {}", deviceId, timeRemaining);
                            takeNap = true;
                        }
                    } catch (DeviceProcessLaunchException dplEx) {
                        processTracker.updateDeviceProcessStatus(deviceId, DeviceProcessStatus.StartupFailed);
                        throw dplEx;
                    } catch (Exception e) {
                        processTracker.updateDeviceProcessStatus(deviceId, DeviceProcessStatus.StartupFailed);
                        throw new OpenposManagementException(
                           String.format("Unexpected failure during launch of Device Process for '%s'", pi.getDeviceId()),
                           e
                        );
                    }
                } else if (pi.getStatus() == DeviceProcessStatus.Starting) {
                    log.info("Device Process '{}' is still Starting, will check it again in a second. Time remaining: {}", deviceId, timeRemaining);
                    takeNap = true;
                } else if (pi.getStatus() == DeviceProcessStatus.StartupFailed) {
                    break;
                }
                if (takeNap) {
                    try { Thread.sleep(1000); } 
                    catch (InterruptedException ex) {
                        log.warn("Thread interrupted", ex);
                    }
                }
            }
            
            pi = processTracker.getDeviceProcessInfo(deviceId);
            if (pi.getStatus() == DeviceProcessStatus.Starting) {
                // We exhausted the timeout period waiting for the process to finish
                // starting, we'll have to mark it as a startup failure
                processTracker.updateDeviceProcessStatus(deviceId, DeviceProcessStatus.StartupFailed);
            }
            
        } finally {
            if (gotLock) {
                processTracker.unlock(deviceId);
            }
        }
        
        return null;
    }
    
    public DiscoveryResponse constructDiscoveryResponse(DeviceProcessInfo pi) {
        DiscoveryResponse cci = new DiscoveryResponse(
            this.resolveHostname(),
            pi.getPort(),
            makeUrl(config.getClientConnect().getWebServiceBaseUrlTemplate(), pi),
            makeUrl(config.getClientConnect().getSecureWebServiceBaseUrlTemplate(), pi),
            makeUrl(config.getClientConnect().getWebSocketBaseUrlTemplate(), pi),
            makeUrl(config.getClientConnect().getSecureWebSocketBaseUrlTemplate(), pi)
        );        
        return cci;
    }
    
    protected String resolveHostname() {
        return StringUtils.isNotBlank(config.getClientConnect().getHostname()) ? config.getClientConnect().getHostname() : NetworkUtils.getNetworkHostname();
    }
    protected String makeUrl(String urlTemplate, DeviceProcessInfo pi) {
        if (StringUtils.isNotBlank(urlTemplate)) {
            String url = urlTemplate;
            if (url.contains("$host")) {
                url = url.replace("$host", this.resolveHostname());
            }
            
            if (url.contains("$port") && pi.getPort() != null) {
                url = url.replace("$port", pi.getPort().toString());
            }
            
            return url;
        } else {
            return null;
        }
    }
    
}
