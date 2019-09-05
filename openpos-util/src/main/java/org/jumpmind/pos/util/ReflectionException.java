package org.jumpmind.pos.util;


public class ReflectionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReflectionException() {
        super();
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(String message) {
        super(message);
    }
    
    public ReflectionException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }

}
