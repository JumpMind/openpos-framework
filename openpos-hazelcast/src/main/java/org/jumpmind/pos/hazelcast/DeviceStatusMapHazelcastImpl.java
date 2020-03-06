package org.jumpmind.pos.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.jumpmind.pos.core.device.DeviceStatus;
import org.jumpmind.pos.util.event.AppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
public class DeviceStatusMapHazelcastImpl implements IDeviceStatusMap {
    private static final String DEVICES_MAP_NAME = "devices-map";

    @Autowired
    DistributedMapProvider mapProvider;

    @Autowired
    HazelcastInstance hz;

    @Override
    public ConcurrentMap<String, DeviceStatus> map() {
        return mapProvider.getMap(DEVICES_MAP_NAME, String.class, DeviceStatus.class);
    }

    public ConcurrentMap<String, String> map2() {
        return mapProvider.getMap("string-map", String.class, String.class);
    }

    @Override
    public void touch(String deviceId) {
        map().compute(deviceId, (k, deviceStatus) -> {
            DeviceStatus status = deviceStatus;
            if (status != null) {
                status = deviceStatus.shallowCopy();
                status.setLastActiveTimeMs(System.currentTimeMillis());
            } else {
                status = new DeviceStatus(deviceId, hz.getLocalEndpoint().getUuid().toString());
            }

            return status;
        });
    }

    @Override
    public void update(AppEvent event) {
        /*
        map2().compute(event.getDeviceId(), (k, v) -> {
            String status = v;
            status = System.currentTimeMillis() + "";
            return status;
        });
         */

        map().compute(event.getDeviceId(), (k, deviceStatus) -> {
            DeviceStatus status = deviceStatus;
            if (status == null) {
                status = new DeviceStatus(event.getDeviceId(), hz.getLocalEndpoint().getUuid().toString());
            } else {
                status = deviceStatus.shallowCopy();
            }
            status.setLastActiveTimeMs(System.currentTimeMillis());
            status.setLatestEvent(event);
            return status;
        });
    }

}
