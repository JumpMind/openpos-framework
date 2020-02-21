package org.jumpmind.pos.devices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class DeviceNotAuthorizedException extends DevicesException {
    public DeviceNotAuthorizedException() {
        super("Device not authorized");
    }
}
