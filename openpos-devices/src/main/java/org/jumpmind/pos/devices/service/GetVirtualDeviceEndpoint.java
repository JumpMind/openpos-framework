package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.VirtualDeviceRepository;
import org.jumpmind.pos.devices.service.model.GetDeviceRequest;
import org.jumpmind.pos.devices.service.model.GetDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path="/devices/device", implementation = "virtual")
public class GetVirtualDeviceEndpoint {

    @Autowired
    VirtualDeviceRepository devicesRepository;

    public GetDeviceResponse getDevice(GetDeviceRequest request) {
        DeviceModel deviceModel = devicesRepository.getByDeviceId(request.getDeviceId());
        if (deviceModel != null) {
            return GetDeviceResponse.builder()
                    .deviceModel(deviceModel)
                    .build();
        } else {
            throw new DeviceNotFoundException();
        }
    }
}
