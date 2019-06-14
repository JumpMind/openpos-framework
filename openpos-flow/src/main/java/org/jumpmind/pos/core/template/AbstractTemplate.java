package org.jumpmind.pos.core.template;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.core.model.SystemStatus;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public abstract class AbstractTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Put properties in this map if they are optional. When not set, they don't
     * show up in the json which means less overhead.
     */
    private Map<String, Object> optionalProperties = new HashMap<String, Object>();

    private String type;
    private SystemStatus systemStatus;

    protected AbstractTemplate(String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.optionalProperties;
    }

    @JsonAnySetter
    public void put(String name, Object value) {
        this.optionalProperties.put(name, value);
    }
    
    public void disableDevMenu() {
        this.put("disableDevMenu", true);
    }

    public SystemStatus getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(SystemStatus systemStatus) {
        this.systemStatus = systemStatus;
    }

}
