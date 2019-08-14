package org.jumpmind.pos.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@ApiIgnore
public class ClientLogService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.POST, value = "api/appId/{appId}/deviceId/{deviceId}/clientlogs")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void clientlogs(
            @PathVariable String appId,
            @PathVariable String deviceId,
            @RequestBody List<ClientLogEntry> clientLogEntries
            ){
        for (ClientLogEntry clientLogEntry : clientLogEntries) {
            String message = "Client Log for device: " + deviceId + ": " + clientLogEntry.getMessage();
            if(clientLogEntry.getType() != null){
                switch(clientLogEntry.getType()){
                    case info:
                    case log:
                        logger.info(message);
                        break;
                    case warn:
                        logger.warn(message);
                        break;
                    case error:
                        logger.error(message);
                        break;
                    case debug:
                        logger.debug(message);
                        break;
                }
            }

        }
    }
}
