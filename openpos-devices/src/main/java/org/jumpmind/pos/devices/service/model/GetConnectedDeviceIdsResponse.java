package org.jumpmind.pos.devices.service.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class GetConnectedDeviceIdsResponse {
    Set<String> deviceIds;
}
