package org.jumpmind.pos.core.device.logfiledownload;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.device.IDeviceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogfileListResponse extends LogfileDownloadResponse {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = 1L;
    
    public LogfileListResponse(IDeviceResponse deviceResponse) {
        super(deviceResponse);
    }
    
    @Override
    public List<String> getPayload() {
        Object payload = super.getPayload();
        if (payload instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<String> loglist = (List<String>) super.getPayload();
            return loglist;
        } else {
            logger.warn("Expected payload of type 'List<String>', but got payload of type: {}.  payload: {}", payload != null ? payload.getClass().getSimpleName() : "null", payload);
            return new ArrayList<String>();
        }
    }
}
