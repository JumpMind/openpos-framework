package org.jumpmind.pos.util.event;

import org.springframework.context.ApplicationEvent;

public class Event extends ApplicationEvent {

    public Event() {
        super("event broadcaster");
    }

    public Event(Object source) {
        super(source);
    }

}
