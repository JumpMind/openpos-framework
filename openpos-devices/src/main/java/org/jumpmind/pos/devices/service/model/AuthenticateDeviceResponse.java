package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.devices.model.DeviceModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateDeviceResponse {
    private DeviceModel deviceModel;
}
