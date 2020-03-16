package org.jumpmind.pos.hazelcast;

import org.jumpmind.pos.util.event.AppEvent;

/**
 * Provides a means to publish AppEvents via Hazelcast without using Spring's
 * event publishing mechanism.
 */
public interface IEventDistributor {

    public void distribute(AppEvent event);

}
