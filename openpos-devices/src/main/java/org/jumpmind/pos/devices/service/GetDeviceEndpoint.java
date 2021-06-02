package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.GetDeviceRequest;
import org.jumpmind.pos.devices.service.model.GetDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import static org.jumpmind.pos.util.RestApiSupport.REST_API_CONTEXT_PATH;

@Endpoint(path = REST_API_CONTEXT_PATH + "/devices/device")
public class GetDeviceEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    public GetDeviceResponse getDevice(GetDeviceRequest request) {
            return GetDeviceResponse.builder()
                    .deviceModel(devicesRepository.getDevice(request.getDeviceId(), request.getAppId()))
                    .build();
        }

    }
