package org.jumpmind.pos.devices.service;


import org.jumpmind.pos.devices.model.DevicePersonalizationModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.PersonalizeMeResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestParam;

@Endpoint(path = "/admin/personalizeMe")
public class GetDevicePersonalizationModelEndpoint {
    @Autowired
    private DevicesRepository repository;

    public PersonalizeMeResponse personalizeMe(@RequestParam("deviceName") String deviceName){
        DevicePersonalizationModel model = repository.findDevicePersonalizationModel(deviceName);
        if(model != null) {
            return new PersonalizeMeResponse(model);
        }
        return null;
    }
}

