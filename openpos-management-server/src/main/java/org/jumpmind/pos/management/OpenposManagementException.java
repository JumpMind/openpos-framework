package org.jumpmind.pos.management;

/**
 * Exception base class for exceptions related to the Openpos Management Server.
 */
public class OpenposManagementException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public OpenposManagementException() {
        super();
    }

    public OpenposManagementException(String message) {
        super(message);
    }

    public OpenposManagementException(Throwable cause) {
        super(cause);
    }
    
    public OpenposManagementException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
