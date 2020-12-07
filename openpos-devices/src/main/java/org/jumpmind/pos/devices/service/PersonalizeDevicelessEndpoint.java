package org.jumpmind.pos.devices.service;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.devices.DeviceNotAuthorizedException;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.devices.DeviceUpdater;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DeviceParamModel;
import org.jumpmind.pos.devices.model.DevicelessRepository;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.PersonalizationRequest;
import org.jumpmind.pos.devices.service.model.PersonalizationResponse;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.pos.util.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
@Endpoint(path="/devices/personalize", implementation = "deviceless")
public class PersonalizeDevicelessEndpoint {

    @Autowired
    DevicelessRepository devicesRepository;

    RandomString session = new RandomString(9);

    @Value("${openpos.general.defaultAppId:pos}")
    String defaultAppId;

    public PersonalizationResponse personalize(@RequestBody PersonalizationRequest request){
        String authToken = request.getDeviceToken();
        DeviceModel deviceModel = null;
        if (authToken != null) {
            deviceModel = devicesRepository.getByAuthToken(authToken);
        }

        if (deviceModel == null) {
             authToken = request.getDeviceToken() != null ? request.getDeviceToken() : session.nextString();
             deviceModel = new DeviceModel();
             deviceModel.setAppId(defaultAppId);
             deviceModel.setDeviceId(authToken);
             deviceModel.setDeviceParamModels(new ArrayList<>(0));
             devicesRepository.add(authToken, deviceModel);
        }

        return PersonalizationResponse.builder()
                .authToken(authToken)
                .deviceModel(deviceModel)
                .build();
    }
}
