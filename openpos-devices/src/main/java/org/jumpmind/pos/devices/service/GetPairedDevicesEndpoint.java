package org.jumpmind.pos.devices.service;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.GetPairedDevicesRequest;
import org.jumpmind.pos.devices.service.model.GetPairedDevicesResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Endpoint(path = "/devices/paired")
public class GetPairedDevicesEndpoint {
    @Autowired
    DevicesRepository devicesRepository;

    public GetPairedDevicesResponse getPairedDevices(GetPairedDevicesRequest request) {
        List<DeviceModel> pairedDevices;

        if (StringUtils.isBlank(request.getAppId())) {
            pairedDevices = devicesRepository.getPairedDevices(request.getBusinessUnitId());
        } else {
            pairedDevices = devicesRepository.getPairedDevicesByAppId(request.getBusinessUnitId(), request.getAppId());
        }

        return new GetPairedDevicesResponse(pairedDevices);
    }
}
