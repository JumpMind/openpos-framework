package org.jumpmind.pos.devices.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DevicelessRepository {
    Map<String, DeviceModel> virtualDevices = new HashMap<>();

    public void add(String authToken, DeviceModel deviceModel) {
        virtualDevices.put(authToken, deviceModel);
    }

    public DeviceModel getByAuthToken(String authToken) {
        return virtualDevices.get(authToken);
    }

    public DeviceModel getByDeviceIdAppId(String deviceId, String appId) {
        return virtualDevices.values().stream().filter(d->d.getDeviceId().equals(deviceId) && d.getAppId().equals(appId)).findFirst().orElse(null);
    }

    public void removeByDeviceIdAppId(String deviceId, String appId) {
        String matchingAuthId = null;
        for (Map.Entry<String,DeviceModel> e : virtualDevices.entrySet()) {
            if (e.getValue().getAppId().equals(appId) && e.getValue().getDeviceId().equals(deviceId)) {
                matchingAuthId = e.getKey();
            }
        }

        if (matchingAuthId != null) {
            virtualDevices.remove(matchingAuthId);
        }
    }
}
