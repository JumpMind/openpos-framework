package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.UnpairDeviceRequest;
import org.jumpmind.pos.devices.service.model.UnpairDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path = "/devices/unpair")
public class UnpairDeviceEndpoint {
    @Autowired
    DevicesRepository devicesRepository;

    public UnpairDeviceResponse unpairDevice(UnpairDeviceRequest request) {
        devicesRepository.unpairDevice(
                request.getDeviceId(),
                request.getPairedDeviceId()
        );

        return UnpairDeviceResponse.builder()
                .device(devicesRepository.getDevice(request.getDeviceId()))
                .unpairedDevice(devicesRepository.getDevice(request.getPairedDeviceId()))
                .build();
    }
}
