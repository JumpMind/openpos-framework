package org.jumpmind.pos.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.topic.ITopic;
import lombok.extern.slf4j.Slf4j;

import org.jumpmind.pos.core.event.DeviceHeartbeatEvent;
import org.jumpmind.pos.util.event.AppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Profile("hazelcast")
public class HazelcastPublisher implements IEventDistributor, ApplicationListener<AppEvent> {

    @Autowired
    HazelcastInstance hz;

    @Autowired
    DeviceStatusMapHazelcastImpl deviceStatusMap;

    public void onApplicationEvent(AppEvent event) {
        // DeviceHeartbeats are handled in the DeviceStatusMapHazelcastImpl
        if (!(event instanceof DeviceHeartbeatEvent)) {
            try {
                if (!event.isRemote()) {
                    log.info("{} received an event {},{} from {}. PUBLISHING IT ", this.getClass().getSimpleName(), event.toString(), System.identityHashCode(event), event.getSource());
                    // then share it with the world
                    ITopic<AppEvent> topic = hz.getTopic("nucommerce/events");
                    topic.publish(event);
                } else {
                    log.info("{} received an event {},{} from {}.  It was from a remote node already.  NOT PUBLISHING ", this.getClass().getSimpleName(), event.toString(), System.identityHashCode(event), event.getSource());
                }
            } catch (HazelcastInstanceNotActiveException e) {
                log.info("Hazelcast was not active.  This is probably because we are shutting down");
            }
        }
    }

    @Async
    @Override
    public void distribute(AppEvent event) {
        onApplicationEvent(event);
        deviceStatusMap.updateDeviceStatus(event);
    }
}
