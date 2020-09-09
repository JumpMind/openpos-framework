package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.model.*;
import org.jumpmind.pos.devices.service.model.PersonalizationConfigResponse;
import org.jumpmind.pos.devices.service.model.PersonalizationParameters;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.pos.util.model.DeviceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Endpoint(path="/devices/personalizationConfig")
public class GetPersonalizationConfigEndpoint {

    @Autowired(required=false)
    PersonalizationParameters personalizationParameters;
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    DevicesRepository repository;

    public PersonalizationConfigResponse getPersonalizationConfig(){
        logger.info("Received a personalization request");

        if(personalizationParameters == null){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No personalization configuration, use default", null);
        }

        List<DeviceStatusModel> disconnectedDevices = repository.getDevicesByStatus(DeviceStatusConstants.DISCONNECTED);
        Map<String, String> availableDevices = null;
        if( disconnectedDevices != null && disconnectedDevices.size() > 0){
            availableDevices = disconnectedDevices.stream()
                    .map( deviceStatusModel -> new DeviceAuthModel(deviceStatusModel.getDeviceId(), deviceStatusModel.getAppId(), repository.getDeviceAuth(deviceStatusModel.getDeviceId(), deviceStatusModel.getAppId()) ))
                    .collect(Collectors.toMap( DeviceAuthModel::getAuthToken, m -> m.getDeviceId() + "/" + m.getAppId()));
        }


        return PersonalizationConfigResponse.builder()
                .devicePattern(personalizationParameters.getDevicePattern())
                .parameters(personalizationParameters.getParameters())
                .availableDevices(availableDevices)
                .build();
    }
}
