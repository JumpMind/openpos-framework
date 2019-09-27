package org.jumpmind.pos.util.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {


    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Async
    public void publish(ApplicationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

}