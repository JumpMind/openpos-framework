package org.jumpmind.pos.core.model;

import org.jumpmind.pos.util.model.Message;

public class ProxyMessage extends Message {

    private static final long serialVersionUID = 1L;
    
    String subType;
    
    Object payload;

    public ProxyMessage() {
        this.setType(MessageType.Proxy);
    }
    
    public void setSubType(String subType) {
        this.subType = subType;
    }
    
    public String getSubType() {
        return subType;
    }
    
    public void setPayload(Object payload) {
        this.payload = payload;
    }
    
    public Object getPayload() {
        return payload;
    }
}
