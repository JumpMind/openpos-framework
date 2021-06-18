package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.FindDevicesRequest;
import org.jumpmind.pos.devices.service.model.FindDevicesResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path = "/devices/find")
public class FindDevicesEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    public FindDevicesResponse findDevices(FindDevicesRequest request) {
       return FindDevicesResponse.builder().
               devices(
                       devicesRepository.findDevices(request.getBusinessUnitId())).build();
    }
}
