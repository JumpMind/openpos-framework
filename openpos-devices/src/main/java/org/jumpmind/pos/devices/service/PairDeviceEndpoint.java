package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.PairDeviceRequest;
import org.jumpmind.pos.devices.service.model.PairDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

@Endpoint(path = "/devices/pair")
public class PairDeviceEndpoint {
    @Autowired
    DevicesRepository devicesRepository;

    public PairDeviceResponse pairDevice(PairDeviceRequest request) {
        devicesRepository.pairDevice(
                request.getDeviceId(),
                request.getAppId(),
                request.getPairedDeviceId(),
                request.getPairedAppId()
        );

        return PairDeviceResponse.builder()
                .device(devicesRepository.getDevice(request.getDeviceId(), request.getAppId()))
                .pairedDevice(devicesRepository.getDevice(request.getPairedDeviceId(), request.getPairedAppId()))
                .build();
    }
}
