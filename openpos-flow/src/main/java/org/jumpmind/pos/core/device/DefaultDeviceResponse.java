package org.jumpmind.pos.core.device;

import java.io.Serializable;

public class DefaultDeviceResponse implements IDeviceResponse, Serializable {
    private static final long serialVersionUID = 1L;
    
    private String requestId;
    private String deviceId;
    private String type;
    private Object payload;
    
    public DefaultDeviceResponse() {
    }
    
    public DefaultDeviceResponse(String requestId, String deviceId, String type, String payload) {
        this.requestId = requestId;
        this.deviceId = deviceId;
        this.type = type;
        this.payload = payload;
    }
    
    protected DefaultDeviceResponse(IDeviceResponse otherResponse) {
        this.requestId = otherResponse.getRequestId();
        this.deviceId = otherResponse.getDeviceId();
        this.type = otherResponse.getType();
        // Shallow copy of the payload
        this.payload = otherResponse.getPayload();
    }
    
    @Override
    public String getRequestId() {
        return this.requestId;
    }
    
    @Override
    public void setRequestId(String sourceRequestId) {
        this.requestId = sourceRequestId;
    }
    
    @Override
    public String getDeviceId() {
        return this.deviceId;
    }
    
    @Override
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    @Override
    public String getType() {
        return this.type;
    }
    
    @Override
    public void setType(String responseType) {
        this.type = responseType;
    }
    
    @Override
    public Object getPayload() {
        return this.payload;
    }
    
    @Override
    public void setPayload(Object payload) {
        this.payload = payload;
    }
    

}
