package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.devices.model.DeviceModel;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUnpairedDevicesResponse {
    List<DeviceModel> devices;
}
