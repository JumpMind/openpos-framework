package org.jumpmind.pos.cache.service;

import org.jumpmind.pos.service.Endpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Transactional
public class SetValueEndpoint {
    
    @Endpoint("/setValue")
    public CacheResult setValue(
            @RequestParam(value="nodeId") String nodeId,        
            @RequestParam(value="cacheId", defaultValue="*") String cacheId,
            @RequestParam(value="key") String key,
            @RequestParam(value="key") Object value) {
        return null;
    }

}
