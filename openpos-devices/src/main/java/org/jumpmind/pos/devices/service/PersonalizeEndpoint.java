package org.jumpmind.pos.devices.service;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.devices.DeviceNotAuthorizedException;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.devices.DeviceUpdater;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DeviceParamModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.PersonalizationRequest;
import org.jumpmind.pos.devices.service.model.PersonalizationResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Endpoint(path="/devices/personalize")
public class PersonalizeEndpoint {

    @Autowired
    DevicesRepository devicesRepository;

    @Autowired
    DeviceUpdater deviceUpdater;

    public PersonalizationResponse personalize(@RequestBody PersonalizationRequest request){
        String authToken = request.getDeviceToken();
        final String deviceId = request.getDeviceId();
        final String appId = request.getAppId();
        final String pairedDeviceId = request.getPairedDeviceId();

        DeviceModel deviceModel;

        if(isNotBlank(deviceId) && isNotBlank(appId)){
            // TODO add a configuration map of appIds that are allowed to share deviceIds. IE probably shouldn't allow a self-checkout share with pos
            try{
                log.info("Validating auth request of {} as {}", deviceId, appId);
                String auth = devicesRepository.getDeviceAuth(request.getDeviceId());

                if( !auth.equals(authToken)) {
                    throw new DeviceNotAuthorizedException();
                }

            } catch (DeviceNotFoundException ex){
                log.info("Registering {} as {}", deviceId, appId);
                // if device doesn't exist create a new unique code
                authToken = UUID.randomUUID().toString();
                devicesRepository.saveDeviceAuth(deviceId, authToken);
            }

            deviceModel = new DeviceModel();
            deviceModel.setAppId(appId);
            deviceModel.setDeviceId(deviceId);
            deviceModel.setPairedDeviceId(pairedDeviceId);
            if( request.getPersonalizationParameters() != null ) {
                deviceModel.setDeviceParamModels(
                        request.getPersonalizationParameters().keySet().stream().map( key -> new DeviceParamModel( key, request.getPersonalizationParameters().get(key))).collect(Collectors.toList())
                );
            }
        } else if ( isNotBlank(authToken)){
            deviceModel = devicesRepository.getDeviceByAuth(authToken);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DeviceId and AppId or AuthToken are required for personalization");
        }

        deviceUpdater.updateDevice(deviceModel);

        return PersonalizationResponse.builder()
                .authToken(authToken)
                .deviceModel(deviceModel)
                .build();
    }
}
