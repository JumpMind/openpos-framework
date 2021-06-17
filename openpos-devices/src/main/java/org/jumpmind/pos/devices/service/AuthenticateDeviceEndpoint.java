package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.DeviceNotAuthorizedException;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DeviceStatusConstants;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.AuthenticateDeviceRequest;
import org.jumpmind.pos.devices.service.model.AuthenticateDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

@Endpoint(path="/devices/authenticate")
public class AuthenticateDeviceEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    public AuthenticateDeviceResponse authenticateDevice(@RequestBody AuthenticateDeviceRequest request){

        try{
            DeviceModel device = devicesRepository.getDeviceByAuth(request.getAuthToken());

            devicesRepository.updateDeviceStatus(device.getDeviceId(), DeviceStatusConstants.CONNECTED);

            return AuthenticateDeviceResponse.builder()
                    .deviceModel(device)
                    .build();
        } catch (DeviceNotFoundException ex) {
            throw new DeviceNotAuthorizedException();
        }

    }

}
