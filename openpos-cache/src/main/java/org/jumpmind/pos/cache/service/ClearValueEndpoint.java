package org.jumpmind.pos.cache.service;

import org.jumpmind.pos.service.Endpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Transactional
public class ClearValueEndpoint {
    
    @Endpoint("/clearValue")
    public CacheResult clearValue(
            @RequestParam(value="nodeId") String nodeId,        
            @RequestParam(value="cacheId", defaultValue="*") String cacheId,
            @RequestParam(value="key") String key) {
        return null;
    }

}
