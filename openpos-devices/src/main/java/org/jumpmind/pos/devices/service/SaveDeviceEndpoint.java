package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.transaction.annotation.Transactional;

@Endpoint(path="/devices/device")
@Transactional(transactionManager = "devTxManager")
public class SaveDeviceEndpoint {

    DevicesRepository devicesRepository;

    public void saveDevice(DeviceModel device) {
        devicesRepository.saveDevice(device);
    }
    
}
