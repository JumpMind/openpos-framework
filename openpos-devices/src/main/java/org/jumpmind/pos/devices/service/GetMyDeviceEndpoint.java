package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.GetDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path="/devices/myDevice")
public class GetMyDeviceEndpoint {

    @Autowired
    ClientContext clientContext;

    @Autowired
    DevicesRepository devicesRepository;

    public GetDeviceResponse getMyDevice(){
        return GetDeviceResponse.builder()
                .deviceModel(devicesRepository.getDevice(clientContext.get("deviceId")))
                .build();
    }
}
