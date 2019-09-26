package org.jumpmind.pos.util.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEventHandler {

    final Logger log = LoggerFactory.getLogger(getClass());

    @OnEvent
    public void logEvent(Event event) {
         log.info("An event of {} was received by the system", event);
    }
}
