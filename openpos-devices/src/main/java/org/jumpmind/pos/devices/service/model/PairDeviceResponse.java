package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.devices.model.DeviceModel;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PairDeviceResponse {
    private DeviceModel device;
    private DeviceModel pairedDevice;
}
