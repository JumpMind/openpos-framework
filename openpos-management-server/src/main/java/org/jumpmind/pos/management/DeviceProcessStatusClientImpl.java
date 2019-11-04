package org.jumpmind.pos.management;

import java.time.Duration;

import org.jumpmind.pos.util.model.ProcessInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Calls the 'status' web service on a remote OpenPOS server process to confirm
 * it is alive.
 */
@Slf4j
@Service
public class DeviceProcessStatusClientImpl implements DeviceProcessStatusClient {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    
    @Autowired
    OpenposManagementServerConfig config;

    @Override
    public ProcessInfo getRemoteProcessStatus(String deviceId, int port) {
        String url = "";
        try {
            url = String.format(config.getStatusUrlTemplate(), port);
            RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(config.getStatusMaxWaitMillis()))
                .setReadTimeout(Duration.ofMillis(config.getStatusMaxWaitMillis()))
                .build();
            return restTemplate.getForObject(url, ProcessInfo.class);
        } catch (RestClientException ex) {
            log.warn("Failed to get device process status for deviceId '{}' at endpoint '{}'. Reason: {}", deviceId, url, ex.getMessage());
            return null;
        }
    }
    
    public RestTemplateBuilder getRestTemplateBuilder() {
        return restTemplateBuilder;
    }

    public void setRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }


}
