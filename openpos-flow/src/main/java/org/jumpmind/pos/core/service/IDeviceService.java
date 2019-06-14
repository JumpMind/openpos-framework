package org.jumpmind.pos.core.service;

import java.util.concurrent.CompletableFuture;

import org.jumpmind.pos.core.device.IDeviceRequest;
import org.jumpmind.pos.core.device.IDeviceResponse;

public interface IDeviceService {
    CompletableFuture<IDeviceResponse> send(String appId, String nodeId, IDeviceRequest request);
}
