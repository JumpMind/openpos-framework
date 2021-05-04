package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.devices.model.DevicePersonalizationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalizeMeResponse {
    private String deviceName;
    private String serverAddress;
    private String serverPort;
    private String deviceId;
    private String appId;
    private String personalizationParams;

    public PersonalizeMeResponse(DevicePersonalizationModel model) {
        this.deviceName = model.getDeviceName();
        this.serverAddress = model.getServerAddress();
        this.serverPort = model.getServerPort();
        this.deviceId = model.getDeviceId();
        this.appId = model.getAppId();
        this.personalizationParams = model.getPersonalizationParams();
    }
}
