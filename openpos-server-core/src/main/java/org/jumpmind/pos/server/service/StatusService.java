package org.jumpmind.pos.server.service;

import java.lang.management.ManagementFactory;

import javax.annotation.PostConstruct;

import org.jumpmind.pos.util.model.ProcessInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@Slf4j
public class StatusService {
    @Autowired
    private Environment env;

    private Integer port;
    private Integer pid;

    @PostConstruct
    public void init() {
        initProcessInfo();
    }
    
    private void initProcessInfo() {
        // TODO? May need to hook into spring's ApplicationPidFileWriter to get PID if this
        // doesn't work in all cases
        String procStr = ManagementFactory.getRuntimeMXBean().getName();
        pid = null;
        if (procStr != null) {
            String[] parts = procStr.split("@");
            if (parts != null && parts.length > 1) {
                try { pid = Integer.valueOf(parts[0]); } catch (Exception ex) {
                   log.warn("Failed to parse pid from {}", procStr);
                }
            }
        }
        port = null;
        String portStr = env.getProperty("local.server.port");
        if (portStr == null || portStr.isEmpty()) {
            portStr = env.getProperty("server.port");
        }
        if (portStr != null) {
            try { port = Integer.valueOf(portStr); } catch (Exception ex) {
                log.warn("Failed to parse port from {}", portStr);
             }
        }
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "status", produces="application/json")
    @ResponseBody
    public ProcessInfo status() {
        log.debug("Received a status request");
        return new ProcessInfo(ProcessInfo.ALIVE_STATUS, port, pid);
    }
    
}
