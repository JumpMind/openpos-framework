package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.GetAllDevicesResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path="/devices/")
public class GetAllDevicesEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    public GetAllDevicesResponse getAllDevices() {
        return GetAllDevicesResponse.builder()
                .devices(devicesRepository.getAllDevices())
                .build();
    }
}
