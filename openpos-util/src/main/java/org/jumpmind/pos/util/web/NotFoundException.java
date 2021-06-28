package org.jumpmind.pos.util.web;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotFoundException() {
        this("");
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
