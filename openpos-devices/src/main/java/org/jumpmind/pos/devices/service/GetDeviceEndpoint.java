package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.GetDeviceRequest;
import org.jumpmind.pos.devices.service.model.GetDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path="/devices/device")
public class GetDeviceEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    public GetDeviceResponse getDevice(GetDeviceRequest request) {
            return GetDeviceResponse.builder()
                    .deviceModel(devicesRepository.getDevice(request.getDeviceId()))
                    .build();
        }

    }
