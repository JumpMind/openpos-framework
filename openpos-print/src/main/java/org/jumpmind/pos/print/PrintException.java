package org.jumpmind.pos.print;

public class PrintException extends RuntimeException {

    public PrintException() {
        super();
    }

    public PrintException(String message) {
        super(message);
    }

    public PrintException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrintException(Throwable cause) {
        super(cause);
    }
}
