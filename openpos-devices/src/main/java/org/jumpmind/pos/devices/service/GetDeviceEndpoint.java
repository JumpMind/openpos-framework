package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.service.Endpoint;

@Endpoint(path="/context/device/{deviceId}")
public class GetDeviceEndpoint {

    DevicesRepository devicesRepository;

    public DeviceModel getDevice(String deviceId) {
        return devicesRepository.findDevice(deviceId);
    }
    
}
