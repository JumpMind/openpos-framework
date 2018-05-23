package org.jumpmind.pos.cache.service;

import org.jumpmind.pos.service.EndpointDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheService {
    
    @Autowired
    private EndpointDispatcher endpointDispatcher;
    
    @RequestMapping
    public CacheResult getValue(
        @RequestParam(value="nodeId") String nodeId,
        @RequestParam(value="key") String key) {
        return endpointDispatcher.dispatch("/getvalue", nodeId, key);
    }

    @RequestMapping("/setValue")
    public CacheResult setValue(
            @RequestParam(value="nodeId") String nodeId,        
            @RequestParam(value="cacheId", defaultValue="*") String cacheId,
            @RequestParam(value="key") String key,
            @RequestParam(value="value") Object value) {
        return endpointDispatcher.dispatch("/setValue", nodeId, cacheId, key, value);
    }    
    
    @RequestMapping("/clearValue")
    public CacheResult clearValue(
            @RequestParam(value="nodeId") String nodeId,        
            @RequestParam(value="cacheId", defaultValue="*") String cacheId,
            @RequestParam(value="key") String key) {
        return endpointDispatcher.dispatch("/clearValue", nodeId, cacheId, key);
    }    

    @RequestMapping("/clearCache")
    public CacheResult clearCache(
            @RequestParam(value="nodeId") String nodeId,        
            @RequestParam(value="cacheId", defaultValue="*") String cacheId) {
        return endpointDispatcher.dispatch("/clearCache", nodeId, cacheId);
    }        

    @RequestMapping("/clearAllCaches")
    public CacheResult clearAllCaches(
            @RequestParam(value="nodeId") String nodeId) {
        return endpointDispatcher.dispatch("/clearAllCaches", nodeId);
    }        
}
