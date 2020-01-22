package org.jumpmind.pos.devices.service.model;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.devices.model.DeviceModel;

@Data
@Builder
public class GetDeviceResponse {
    private DeviceModel deviceModel;
}
