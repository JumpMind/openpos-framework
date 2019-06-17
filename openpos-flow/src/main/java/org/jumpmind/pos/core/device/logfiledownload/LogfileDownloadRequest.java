package org.jumpmind.pos.core.device.logfiledownload;

import org.jumpmind.pos.core.device.DefaultDeviceRequest;

public class LogfileDownloadRequest extends DefaultDeviceRequest {
    private static final long serialVersionUID = 1L;
    public static final String DEVICE_ID = "logfileDownloadPlugin";

    public LogfileDownloadRequest(LogfileDownloadOperation operation) {
        this.setDeviceId(DEVICE_ID);
        this.setSubType(operation != null ? operation.name() : null);
    }
}
