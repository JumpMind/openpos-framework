package org.jumpmind.pos.management;

/**
 * An error that is encountered in the Device Process configuration.
 *
 */
public class DeviceProcessConfigException extends OpenposManagementException {
    private static final long serialVersionUID = 1L;

    public DeviceProcessConfigException() {
        super();
    }

    public DeviceProcessConfigException(String message) {
        super(message);
    }

    public DeviceProcessConfigException(Throwable cause) {
        super(cause);
    }
    
    public DeviceProcessConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
