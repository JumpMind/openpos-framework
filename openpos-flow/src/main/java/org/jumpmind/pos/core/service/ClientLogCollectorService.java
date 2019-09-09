package org.jumpmind.pos.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@ApiIgnore
public class ClientLogCollectorService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public ClientLogCollectorService() {
        logger.info("Client log created");
    }

    @RequestMapping(method = RequestMethod.POST, value = "api/appId/{appId}/deviceId/{deviceId}/clientlogs")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void clientLogs(
            @PathVariable String appId,
            @PathVariable String deviceId,
            @RequestBody List<ClientLogEntry> clientLogEntries
            ){
        MDC.put("appId", appId);
        MDC.put("deviceId", deviceId);
        for (ClientLogEntry clientLogEntry : clientLogEntries) {
            String message = clientLogEntry.getMessage();

            MDC.put("timestamp", clientLogEntry.getTimestamp().toString());
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
