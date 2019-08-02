package org.jumpmind.pos.print;

public class PrintNotSupportedException extends RuntimeException {

    public PrintNotSupportedException() {
        super();
    }

    public PrintNotSupportedException(String message) {
        super(message);
    }

    public PrintNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrintNotSupportedException(Throwable cause) {
        super(cause);
    }
}
