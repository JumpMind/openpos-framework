package org.jumpmind.pos.management;

/**
 * Errors that occur during execution of the process for a given device.
 */
public class DeviceProcessLaunchException extends OpenposManagementException {
    private static final long serialVersionUID = 1L;

    public DeviceProcessLaunchException() {
        super();
    }

    public DeviceProcessLaunchException(String message) {
        super(message);
    }

    public DeviceProcessLaunchException(Throwable cause) {
        super(cause);
    }
    
    public DeviceProcessLaunchException(String message, Throwable cause) {
        super(message, cause);
    }

}
