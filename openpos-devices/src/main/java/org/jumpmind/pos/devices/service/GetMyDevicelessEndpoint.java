package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.service.model.GetDeviceRequest;
import org.jumpmind.pos.devices.service.model.GetDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path = "/devices/myDevice", implementation = "deviceless")
public class GetMyDevicelessEndpoint {

    @Autowired
    ClientContext clientContext;

    @Autowired
    GetDevicelessEndpoint endpoint;

    public GetDeviceResponse getMyDevice() {
        return GetDeviceResponse.builder()
                .deviceModel(endpoint.getDevice(
                        GetDeviceRequest.builder().
                                deviceId(clientContext.get("deviceId")).
                                appId(clientContext.get("appId")).build()).getDeviceModel())
                .build();
    }
}
