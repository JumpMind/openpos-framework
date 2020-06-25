package org.jumpmind.pos.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.PostConstruct;

@RestController
@ApiIgnore
public class ClientLogCollectorService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${openpos.clientLogCollector.timestampFormat:#{null}}")
    protected String timestampFormat;
    
    protected DateTimeFormatter timestampFormatter;
    
    public ClientLogCollectorService() {
        logger.info("Client log created");
    }
    
    @PostConstruct
    protected void init() {
        if (timestampFormat != null) {
            try {
                this.timestampFormatter = DateTimeFormatter.ofPattern(timestampFormat).withZone(ZoneId.systemDefault());
            } catch (Exception ex) {
                logger.error("openpos.clientLogCollector.timestampFormat value of '{}' is not valid.", this.timestampFormat);
            }
        }
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

            if (timestampFormatter != null) {
                MDC.put("timestamp", timestampFormatter.format(clientLogEntry.getTimestamp().toInstant()));
            } else {
                MDC.put("timestamp", clientLogEntry.getTimestamp().toString());
            }
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
