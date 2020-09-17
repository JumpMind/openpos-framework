package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.devices.model.DeviceAuthModel;
import org.jumpmind.pos.devices.model.DeviceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalizationConfigResponse {
    private String devicePattern;
    private List<PersonalizationParameter> parameters = new ArrayList<>();
    private Map<String, String> availableDevices;
    private List<String> loadedAppIds;

}
