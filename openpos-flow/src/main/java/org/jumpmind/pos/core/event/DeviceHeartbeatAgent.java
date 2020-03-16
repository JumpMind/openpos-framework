package org.jumpmind.pos.core.event;

import javax.annotation.PostConstruct;

import org.jumpmind.pos.core.flow.StateManagerContainer;
import org.jumpmind.pos.util.event.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Sends periodic DeviceHeartbeatEvents for all the active devices in the system.
 * Use the {@code openpos.general.deviceHeartbeatDelayMs} property to control
 * how often events are published.  By default, every 30 seconds.
 */
@Component
@Profile("hazelcast")
@Slf4j
public class DeviceHeartbeatAgent {

    @Autowired
    StateManagerContainer stateManagerContainer;
    
    @Autowired
    EventPublisher eventPublisher;
    
    @PostConstruct
    public void init() {
        log.info("DeviceHeartbeatAgent is enabled and running");
    }
    
    @Scheduled(fixedDelayString = "${openpos.general.deviceHeartbeatDelayMs:30000}")
    public void heartbeat() {
        stateManagerContainer.getAllStateManagers().forEach(stateManager -> {
            if (stateManager.getAppId() != null) {
                eventPublisher.publish(new DeviceHeartbeatEvent(stateManager.getDeviceId()));
            }
        });
    }
}
