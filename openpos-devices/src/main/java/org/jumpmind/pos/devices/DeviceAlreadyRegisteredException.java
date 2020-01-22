package org.jumpmind.pos.devices;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@ResponseStatus(code = HttpStatus.CONFLICT)
public class DeviceAlreadyRegisteredException extends DevicesException {

    private String deviceId;
    private String appId;

    public DeviceAlreadyRegisteredException(String deviceId, String appId) {
        super("A device with id " + deviceId + " and app id " + appId + " is already registered");
        this.deviceId = deviceId;
        this.appId = appId;
    }
}
