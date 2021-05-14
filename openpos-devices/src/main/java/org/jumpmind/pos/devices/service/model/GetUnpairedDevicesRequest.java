package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUnpairedDevicesRequest {
    private String businessUnitId;
    private String appId;
}
