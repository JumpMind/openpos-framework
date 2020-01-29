package org.jumpmind.pos.util.clientcontext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ClientContext {
    private InheritableThreadLocal<Map<String, String>> propertiesMap = new InheritableThreadLocal<>();
    final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${openpos.installationId:'not set'}")
    String installationId;

    public void put(String name, String value) {
        if(propertiesMap.get() == null) {
            propertiesMap.set( new HashMap<>());
        }

        propertiesMap.get().put(name, value);
    }

    public String get(String name) {
        Map<String, String> props = propertiesMap.get();

        if( props == null || !props.containsKey(name) ){
            if("deviceId".equals(name)){
                return installationId;
            }
            if("appId".equals(name)){
                return "server";
            }
            log.warn("ClientContext property '" + name + "' not found in ClientContext map.");
            return null;
        }

        return props.get(name);
    }

    public Set<String> getPropertyNames() {
        Map<String, String> props = propertiesMap.get();

        if( props == null ){
            return new HashSet<>();
        }

        return props.keySet();
    }

    public void clear() {
        propertiesMap.set(null);
    }
}
