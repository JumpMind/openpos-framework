package org.jumpmind.pos.util.clientcontext;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ClientContext {
    private InheritableThreadLocal<Map<String, String>> propertiesMap = new InheritableThreadLocal<>();

    public void put(String name, String value) {
        if(propertiesMap.get() == null) {
            propertiesMap.set( new HashMap<>());
        }

        propertiesMap.get().put(name, value);
    }

    public String get(String name) throws ClientContextPropertyException {
        Map<String, String> props = propertiesMap.get();

        if( props == null || !props.containsKey(name) ){
            throw new ClientContextPropertyException("ClientContext property '" + name + "' not found in ClientContext map.");
        }

        return props.get(name);
    }

    public Set<String> getPropertyNames() throws ClientContextPropertyException {
        Map<String, String> props = propertiesMap.get();

        if( props == null ){
            return new HashSet<>();
        }

        return props.keySet();
    }
}
