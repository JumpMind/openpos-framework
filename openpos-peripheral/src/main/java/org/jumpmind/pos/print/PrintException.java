package org.jumpmind.pos.print;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.EnumSet;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintException extends RuntimeException {
    public enum StatusCode {
        CoverOpened,
        OutOfPaper
    }

    final EnumSet<StatusCode> status;

    private static String createMessageFromStatus(EnumSet<StatusCode> status) {
        final StringBuilder msg = new StringBuilder("printer operation failed");

        for (StatusCode code : status) {
            String codeMsg;

            switch (code) {
                case CoverOpened:
                    codeMsg = "cover opened";
                    break;

                case OutOfPaper:
                    codeMsg = "out of paper";
                    break;

                default:
                    continue;
            }

            msg.append("; ");
            msg.append(codeMsg);
        }

        return msg.toString();
    }

    public PrintException() {
        this("printer operation failed");
    }

    public PrintException(EnumSet<StatusCode> status) {
        this(createMessageFromStatus(status), status);
    }

    public PrintException(String message) {
        this(message, EnumSet.noneOf(StatusCode.class));
    }

    public PrintException(String message, EnumSet<StatusCode> status) {
        this(message, status, null);
    }

    public PrintException(String message, EnumSet<StatusCode> status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public PrintException(String message, Throwable cause) {
        this(message, EnumSet.noneOf(StatusCode.class), cause);
    }

    public PrintException(Throwable cause) {
        this("printer operation failed", cause);
    }

    public String toString() {
        // don't use lombok's toString()
        return super.toString();
    }
}
