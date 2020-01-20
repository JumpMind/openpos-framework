package org.jumpmind.pos.devices.model;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.persist.DBSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DevicesRepository {

    @Autowired
    @Lazy
    DBSession dbSession;

    @Cacheable("/devices/device")
    public DeviceModel findDevice(String deviceId) {
        DeviceModel device = dbSession.findByNaturalId(DeviceModel.class, deviceId);
        if (device != null) {
            return device;
        } else {
            throw new DeviceNotFoundException(deviceId);
        }
    }


    @CacheEvict(value = "/devices/device", key = "#device.deviceId")
    public void saveDevice(DeviceModel device) {
        this.dbSession.save(device);
    }

}
