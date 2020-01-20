package org.jumpmind.pos.devices;

import org.jumpmind.pos.service.PosServerException;

public class DevicesException extends PosServerException {

    private static final long serialVersionUID = 1L;

    public DevicesException() {
        super();
    }

    public DevicesException(String message, Throwable cause) {
        super(message, cause);
    }

    public DevicesException(String message) {
        super(message);
    }

    public DevicesException(Throwable cause) {
        super(cause);
    }
}
