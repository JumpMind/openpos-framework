package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.DeviceNotAuthorizedException;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.AuthenticateDeviceRequest;
import org.jumpmind.pos.devices.service.model.AuthenticateDeviceResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import static org.jumpmind.pos.util.RestApiSupport.REST_API_CONTEXT_PATH;

@Endpoint(path = REST_API_CONTEXT_PATH + "/devices/authenticate")
public class AuthenticateDeviceEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    public AuthenticateDeviceResponse authenticateDevice(@RequestBody AuthenticateDeviceRequest request){

        try{
            return AuthenticateDeviceResponse.builder()
                    .deviceModel(devicesRepository.getDeviceByAuth(request.getAuthToken()))
                    .build();
        } catch (DeviceNotFoundException ex) {
            throw new DeviceNotAuthorizedException();
        }

    }

}
