package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.GetDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.pos.util.clientcontext.ClientContextProperty;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path="/devices/myDevice")
public class GetMyDeviceEndpoint {

    @ClientContextProperty(name = "deviceId")
    String deviceId;

    @ClientContextProperty(name = "appId")
    String appId;

    @Autowired
    DevicesRepository devicesRepository;

    public GetDeviceResponse getMyDevice(){
        return GetDeviceResponse.builder()
                .deviceModel(devicesRepository.getDevice(deviceId, appId))
                .build();
    }
}
