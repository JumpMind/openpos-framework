package org.jumpmind.pos.devices.service;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.devices.service.model.PersonalizationConfigResponse;
import org.jumpmind.pos.service.Endpoint;


@Slf4j
@Endpoint(path = "/devices/personalizationConfig", implementation = "virtual")
public class GetVirtualDevicePersonalizationConfigEndpoint {

    public PersonalizationConfigResponse getPersonalizationConfig() {
        // Not Used
        return PersonalizationConfigResponse.builder().build();
    }
}
