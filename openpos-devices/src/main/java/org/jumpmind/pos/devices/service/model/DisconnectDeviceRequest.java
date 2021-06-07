package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisconnectDeviceRequest {
    private String deviceId;
}
