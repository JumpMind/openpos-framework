package org.jumpmind.pos.cache.service;

import org.jumpmind.pos.service.Endpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Transactional
public class ClearCacheEndpoint {
    
    @Endpoint("/clearCache")
    public CacheResult clearCache(
            @RequestParam(value="nodeId") String nodeId,        
            @RequestParam(value="cacheId", defaultValue="*") String cacheId) {
        return null;
    }

}
