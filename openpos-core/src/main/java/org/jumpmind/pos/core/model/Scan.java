package org.jumpmind.pos.core.model;

import java.io.Serializable;

public class Scan implements Serializable {

    private static final long serialVersionUID = 1L;
    private String value;
    private String format;
    private Boolean cancelled;
    
    public Scan() {     
    }
    
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }

    public Boolean isCancelled() {
        return this.getCancelled() != null && this.getCancelled();
    }
    
    public Boolean isNotCancelled() {
        return ! this.isCancelled();
    }
    
    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }
}
