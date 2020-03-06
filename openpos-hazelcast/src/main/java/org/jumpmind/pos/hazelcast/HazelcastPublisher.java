package org.jumpmind.pos.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.util.event.AppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("hazelcast")
public class HazelcastPublisher implements ApplicationListener<AppEvent> {

    @Autowired
    HazelcastInstance hz;

    @Autowired
    DeviceStatusMapHazelcastImpl deviceStatusMap;

    @EventListener
    public void onApplicationEvent(AppEvent event) {
        if (!event.isRemote()) {
            log.info("{} received an event {},{} from {}. PUBLISHING IT ", this.getClass().getSimpleName(), event.toString(), event.hashCode(), event.getSource());
            // then share it with the world
            deviceStatusMap.update(event);
            ITopic<AppEvent> topic = hz.getTopic("nucommerce/events");
            topic.publish(event);
        } else {
            log.info("{} received an event {},{} from {}.  It was from a remote node already.  NOT PUBLISHING ", this.getClass().getSimpleName(), event.toString(), event.hashCode(), event.getSource());

        }
    }
}
