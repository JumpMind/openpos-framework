package org.jumpmind.pos.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.event.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@Profile("hazelcast")
public class HazelcastSubscriber {

    @Autowired
    EventPublisher eventPublisher;

    @Autowired
    HazelcastInstance hz;

    @PostConstruct
    protected void subscribe() {
        ITopic<AppEvent> topic = hz.getTopic("nucommerce/events");
        topic.addMessageListener(new LocalListener());
    }

    private class LocalListener implements MessageListener<AppEvent> {
        public void onMessage(Message<AppEvent> m) {
            AppEvent event = m.getMessageObject();
            if (!m.getPublishingMember().getUuid().equals(hz.getLocalEndpoint().getUuid())) {
                event.setRemote(true);
                log.info("{} received an event from a different endpoint: {}", getClass().getSimpleName(), event);
                eventPublisher.publish(event);
            }
        }
    }
}
