package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.service.model.PersonalizationConfigResponse;
import org.jumpmind.pos.devices.service.model.PersonalizationParameters;
import org.jumpmind.pos.service.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Endpoint(path="/devices/personalizationConfig")
public class GetPersonalizationConfigEndpoint {

    @Autowired(required=false)
    PersonalizationParameters personalizationParameters;
    Logger logger = LoggerFactory.getLogger(getClass());


    public PersonalizationConfigResponse getPersonalizationConfig(){
        logger.info("Received a personalization request");

        if(personalizationParameters == null){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No personalization configuration, use default", null);
        }

        return PersonalizationConfigResponse.builder()
                .devicePattern(personalizationParameters.getDevicePattern())
                .parameters(personalizationParameters.getParameters())
                .build();
    }
}
