package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DevicelessRepository;
import org.jumpmind.pos.devices.service.model.GetDeviceRequest;
import org.jumpmind.pos.devices.service.model.GetDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path="/devices/device", implementation = "deviceless")
public class GetDevicelessEndpoint {

    @Autowired
    DevicelessRepository devicesRepository;

    public GetDeviceResponse getDevice(GetDeviceRequest request) {
        DeviceModel deviceModel = devicesRepository.getByDeviceIdAppId(request.getDeviceId(), request.getAppId());
        if (deviceModel != null) {
            return GetDeviceResponse.builder()
                    .deviceModel(deviceModel)
                    .build();
        } else {
            throw new DeviceNotFoundException();
        }
    }
}
