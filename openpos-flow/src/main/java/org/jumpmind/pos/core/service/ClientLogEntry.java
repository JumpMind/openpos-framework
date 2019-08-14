package org.jumpmind.pos.core.service;

import java.io.Serializable;
import java.util.Date;

public class ClientLogEntry implements Serializable {
    private ClientLogType type;
    private Date timestamp;
    private String message;

    public ClientLogType getType() {
        return type;
    }

    public void setType(ClientLogType type) {
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
