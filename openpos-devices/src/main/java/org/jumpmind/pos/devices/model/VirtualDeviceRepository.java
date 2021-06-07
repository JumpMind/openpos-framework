package org.jumpmind.pos.devices.model;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VirtualDeviceRepository {
    Map<String, DeviceModel> virtualDevices = new ConcurrentHashMap<>();

    public void add(String authToken, DeviceModel deviceModel) {
        virtualDevices.put(authToken, deviceModel);
    }

    public DeviceModel getByAuthToken(String authToken) {
        return virtualDevices.get(authToken);
    }

    public DeviceModel getByDeviceId(String deviceId) {
        return virtualDevices.values().stream().filter(d -> d.getDeviceId().equals(deviceId)).findFirst().orElse(null);
    }

    public void removeByDeviceIdAppId(String deviceId) {
        String matchingAuthId = null;
        for (Map.Entry<String,DeviceModel> e : virtualDevices.entrySet()) {
            if (e.getValue().getDeviceId().equals(deviceId)) {
                matchingAuthId = e.getKey();
            }
        }

        if (matchingAuthId != null) {
            virtualDevices.remove(matchingAuthId);
        }
    }
}
