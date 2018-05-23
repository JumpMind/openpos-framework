package org.jumpmind.pos.persist;


public class PersistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistException() {
        super();
    }

    public PersistException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistException(String message) {
        super(message);
    }

    public PersistException(Throwable cause) {
        super(cause);
    }

}
