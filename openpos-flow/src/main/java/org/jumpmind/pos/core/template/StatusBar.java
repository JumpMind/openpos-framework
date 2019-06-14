package org.jumpmind.pos.core.template;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class StatusBar implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Put properties in this map if they are optional. When not set, they don't
     * show up in the json which means less overhead.
     */
    private Map<String, Object> optionalProperties = new HashMap<String, Object>();
    
    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.optionalProperties;
    }
    
    @JsonAnySetter
    public void put(String name, Object value) {
        this.optionalProperties.put(name, value);
    }

}
