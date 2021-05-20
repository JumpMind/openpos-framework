package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.service.model.GetDeviceRequest;
import org.jumpmind.pos.devices.service.model.GetDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path = "/devices/myDevice", implementation = "virtual")
public class GetMyVirtualDeviceEndpoint {

    @Autowired
    ClientContext clientContext;

    @Autowired
    GetVirtualDeviceEndpoint endpoint;

    public GetDeviceResponse getMyDevice() {
        DeviceModel device = endpoint.getDevice(
                GetDeviceRequest.builder().deviceId(clientContext.get("deviceId")).build()
        ).getDeviceModel();
        return GetDeviceResponse.builder()
                .deviceModel(device)
                .build();
    }
}
