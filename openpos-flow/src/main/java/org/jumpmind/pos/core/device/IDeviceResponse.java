package org.jumpmind.pos.core.device;

public interface IDeviceResponse {
    public final static String DEVICE_RESPONSE_TYPE = "DeviceResponse";
    public final static String DEVICE_ERROR_RESPONSE_TYPE = "DeviceErrorResponse";
    public final static String DEVICE_TIMEOUT_RESPONSE_TYPE = "DeviceTimeoutResponse";
    public final static String DEVICE_DNE_RESPONSE_TYPE = "DeviceDoesNotExistResponse";
    
    String getRequestId();
    void setRequestId(String sourceRequestId);
    String getDeviceId();
    void setDeviceId(String deviceId);
    String getType();
    void setType(String responseType);
    Object getPayload();
    void setPayload(Object payload);
}
