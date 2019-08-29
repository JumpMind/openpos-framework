package org.jumpmind.pos.server.model;

import java.io.Serializable;
import java.util.UUID;

public class ProxyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID messageId;

    private String payload;

    private boolean success = true;

    public ProxyResponse() {
    }

    public ProxyResponse(UUID messageId, String payload) {
        this.messageId = messageId;
        this.payload = payload;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
