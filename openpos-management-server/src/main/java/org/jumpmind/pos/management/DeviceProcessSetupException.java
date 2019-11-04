package org.jumpmind.pos.management;

/**
 * Errors that occur during initialization or instantiation of the working
 * environment for a given device.
 *
 */
public class DeviceProcessSetupException extends OpenposManagementException {
    private static final long serialVersionUID = 1L;

    public DeviceProcessSetupException() {
        super();
    }

    public DeviceProcessSetupException(String message) {
        super(message);
    }

    public DeviceProcessSetupException(Throwable cause) {
        super(cause);
    }
    
    public DeviceProcessSetupException(String message, Throwable cause) {
        super(message, cause);
    }

}
