package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.DeviceNotAuthorizedException;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DevicelessRepository;
import org.jumpmind.pos.devices.service.model.AuthenticateDeviceRequest;
import org.jumpmind.pos.devices.service.model.AuthenticateDeviceResponse;
import org.jumpmind.pos.devices.service.model.PersonalizationRequest;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

@Endpoint(path = "/devices/authenticate", implementation = "deviceless")
public class AuthenticateDevicelessEndpoint {

    @Autowired
    DevicelessRepository devicesRepository;

    @Autowired
    PersonalizeDevicelessEndpoint personalizeDevicelessEndpoint;

    public AuthenticateDeviceResponse authenticateDevice(@RequestBody AuthenticateDeviceRequest request) {
        DeviceModel deviceModel = devicesRepository.getByAuthToken(request.getAuthToken());
        if (deviceModel != null) {
            return AuthenticateDeviceResponse.builder()
                    .deviceModel(deviceModel)
                    .build();
        } else {
            String deviceToken = personalizeDevicelessEndpoint.personalize(PersonalizationRequest.builder().deviceToken(request.getAuthToken()).build()).getAuthToken();
            return authenticateDevice(request);
        }
    }

}
