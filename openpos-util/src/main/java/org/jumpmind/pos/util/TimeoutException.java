package org.jumpmind.pos.util;

public class TimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TimeoutException() {
        super();
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Object... args) {
        super(String.format(message, args));
    }

    public TimeoutException(Throwable cause) {
        super(cause);
    }
}
