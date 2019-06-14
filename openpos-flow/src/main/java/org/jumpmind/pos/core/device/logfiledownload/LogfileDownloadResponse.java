package org.jumpmind.pos.core.device.logfiledownload;

import org.jumpmind.pos.core.device.DefaultDeviceResponse;
import org.jumpmind.pos.core.device.IDeviceResponse;

public abstract class LogfileDownloadResponse extends DefaultDeviceResponse {

    private static final long serialVersionUID = 1L;
    
    LogfileDownloadResponse(IDeviceResponse deviceResponse) {
        super(deviceResponse);
    }

    public LogfileDownloadOperation getOperationType() {
        return LogfileDownloadOperation.valueOf(this.getType());
    }
    
}
