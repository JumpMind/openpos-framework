package org.jumpmind.pos.util.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
        
    String type;
    
    public Message() {
    }

    public Message(String type) {
        this.type = type;
    }
    
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

    public boolean contains(String name) {
        return this.optionalProperties.containsKey(name);
    }

    public <T> T get(String name) {
        return (T)optionalProperties.get(name);
    }

    public void clearAdditionalProperties() {
        this.optionalProperties.clear();
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }


    public void setSequenceNumber(int sequenceNumber) {
        put("sequenceNumber", sequenceNumber);
    }
    
}
