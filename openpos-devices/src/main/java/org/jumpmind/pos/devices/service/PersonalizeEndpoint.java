package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.service.model.PersonalizationRequest;
import org.jumpmind.pos.devices.service.model.PersonalizationResponse;
import org.jumpmind.pos.service.Endpoint;
import org.springframework.web.bind.annotation.RequestBody;

@Endpoint(path="/context/personalizeDevice")
public class PersonalizeEndpoint {
    public PersonalizationResponse personalize(@RequestBody PersonalizationRequest request){

        return PersonalizationResponse.builder().build();
    }
}
