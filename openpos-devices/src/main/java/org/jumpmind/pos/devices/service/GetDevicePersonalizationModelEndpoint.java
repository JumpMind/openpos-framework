package org.jumpmind.pos.devices.service;


import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.devices.model.DevicePersonalizationModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.PersonalizeMeResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestParam;

import static org.jumpmind.pos.util.RestApiSupport.REST_API_CONTEXT_PATH;

@Slf4j
@Endpoint(path = REST_API_CONTEXT_PATH + "/admin/personalizeMe")
public class GetDevicePersonalizationModelEndpoint {
    @Autowired
    private DevicesRepository repository;

    public PersonalizeMeResponse personalizeMe(@RequestParam("deviceName") String deviceName){
        log.info("Receieved auto-personalization request from device \"" + deviceName + "\"");
        DevicePersonalizationModel model = repository.findDevicePersonalizationModel(deviceName);
        if(model != null) {
            return new PersonalizeMeResponse(model);
        }
        return null;
    }
}

