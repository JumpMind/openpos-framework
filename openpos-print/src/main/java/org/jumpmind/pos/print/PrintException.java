package org.jumpmind.pos.print;

import lombok.Data;

@Data
public class PrintException extends RuntimeException {

    static final int PRINT_ERROR_SLIP_INSERT_TIMEOUT = 1;

    int errorCode;

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

    public String toString() {
        // don't use lombok's toString()
        return super.toString();
    }
}
