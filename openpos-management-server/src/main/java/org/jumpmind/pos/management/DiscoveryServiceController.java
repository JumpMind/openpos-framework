package org.jumpmind.pos.management;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class DiscoveryServiceController {

    @Autowired
    ProcessManagerService processManager;

    @GetMapping("personalize")
    public ImpersonalizationResponse impersonalize() {
        log.info("Received a personalize request");
        return makeImpersonalizationResponse();
    }
    
    @GetMapping("ping")
    public String ping() {
        log.info("Received a ping request");
        return "{ \"pong\": \"true\" }";
    }
    
    @GetMapping("discover")
    public DiscoveryResponse getConnectionUrl(
        @RequestParam(required=false) String appId,
        @RequestParam String deviceId,
        HttpServletRequest request
    ) {
        log.info("Received a discovery request for appId: {}, deviceId: {}", appId, deviceId);
        try {
            DeviceProcessInfo pi = processManager.queryOrLaunchDeviceProcess(appId, deviceId);
            if (pi != null) {
                if (pi.getStatus() == DeviceProcessStatus.Running) {
                    return processManager.constructDiscoveryResponse(pi, request);
                } else {
                    log.trace("Nothing to return.  Device Process '{}' status is: {}", pi.getDeviceId(), pi.getStatus());
                    return null;
                }
            } else {
                log.trace("No Device Process Info for '{}'",deviceId);
                return null;
            }
        } catch (Exception ex) {
            log.error(String.format("Failed to launch process for '%s'", deviceId), ex);
            return null;
        }
    }
    
    @Lookup("defaultImpersonalizationResponse")
    ImpersonalizationResponse makeImpersonalizationResponse() {
        // Will be supplied by Spring context
        return null;
    }

}
