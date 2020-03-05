package org.jumpmind.pos.hazelcast;

import org.jumpmind.pos.core.device.DeviceStatus;
import org.jumpmind.pos.util.event.AppEvent;

import java.util.concurrent.ConcurrentMap;

public interface IDeviceStatusMap {
    /**
     * Retrieves the map.  Any changes to the map will be distributed to all openpos cluster members.
     * @return The distributed Device status map, keyed by Device ID;
     */
    ConcurrentMap<String, DeviceStatus> map();

    ConcurrentMap<String, String> map2();

    /**
     * Updates the lastActiveTimeMs for the device.
     * @param deviceId The ID of the device to update.
     */
    void touch(String deviceId);

    void update(AppEvent event);
}
