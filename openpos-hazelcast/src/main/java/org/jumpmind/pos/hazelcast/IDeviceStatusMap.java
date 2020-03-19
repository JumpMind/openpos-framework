package org.jumpmind.pos.hazelcast;

import org.jumpmind.pos.core.device.DeviceStatus;
import org.jumpmind.pos.util.event.AppEvent;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public interface IDeviceStatusMap {
    /**
     * Retrieves the map.  Any changes to the map will be distributed to all openpos cluster members.
     * @return The distributed Device status map, keyed by Device ID;
     */
    ConcurrentMap<String, DeviceStatus> get();

    /**
     * Updates the lastActiveTimeMs for the device.
     * @param deviceId The ID of the device to update.
     */
    void touch(String deviceId);

    /**
     * Sets the given event as the current event for the associated device.
     * This allows consumers of the map to have access to last known event 
     * associated with the device.
     * @param event The current event
     */
    void update(AppEvent event);
    
    /**
     * Provides a means to supply a consumer that will be invoked when a device
     * abnormally disappears from tracking.  The given consumer is invoked once
     * for each device that has disappears.  This could happen if the server
     * that is hosting the session for one or more devices is terminated or
     * abnormally exits.
     * @param onDeviceDisconnect The consumer to invoke once per device
     */
    void setDisappearanceHandler(Consumer<String> onDeviceDisconnect);
}
