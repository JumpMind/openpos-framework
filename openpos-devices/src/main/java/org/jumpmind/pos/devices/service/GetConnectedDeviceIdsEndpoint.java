package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.GetConnectedDeviceIdsRequest;
import org.jumpmind.pos.devices.service.model.GetConnectedDeviceIdsResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Endpoint(path = "/devices/connectedDevices")
public class GetConnectedDeviceIdsEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    @Value("${openpos.installationId:'not set'}")
    String installationId;

    public GetConnectedDeviceIdsResponse getConnectedDeviceIds(GetConnectedDeviceIdsRequest request) {
        return GetConnectedDeviceIdsResponse.builder().deviceIds(
                devicesRepository.getConnectedDevices(request.getBusinessUnitId(), installationId)).build();
    }
}
