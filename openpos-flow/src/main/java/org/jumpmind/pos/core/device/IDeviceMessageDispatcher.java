package org.jumpmind.pos.core.device;


public interface IDeviceMessageDispatcher {
    public IDeviceResponse sendDeviceRequest(IDeviceRequest request);
}
