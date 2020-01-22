package org.jumpmind.pos.devices.service.model;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.devices.model.DeviceModel;

@Builder
@Data
public class SaveDeviceRequest {
    private DeviceModel deviceModel;
    private String authToken;
}
