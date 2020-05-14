package org.jumpmind.pos.core.flow;

/**
 * Used to break out of the actionLoop of the StateManager while resetting.
 */
public class StateManagerResetException extends RuntimeException {

    public StateManagerResetException() {
    }

    public StateManagerResetException(String message) {
        super(message);
    }

    public StateManagerResetException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateManagerResetException(Throwable cause) {
        super(cause);
    }
}
