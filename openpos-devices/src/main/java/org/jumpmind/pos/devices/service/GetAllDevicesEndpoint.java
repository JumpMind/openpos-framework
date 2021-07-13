package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.GetAllDevicesResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import static org.jumpmind.pos.util.RestApiSupport.REST_API_CONTEXT_PATH;

@Endpoint(path = REST_API_CONTEXT_PATH + "/devices/")
public class GetAllDevicesEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    public GetAllDevicesResponse getAllDevices() {
        return GetAllDevicesResponse.builder()
                .devices(devicesRepository.getAllDevices())
                .build();
    }
}
