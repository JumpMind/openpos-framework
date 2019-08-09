package org.jumpmind.pos.util.model;

import java.util.UUID;

import org.jumpmind.pos.util.model.Message;

public class ProxyMessage extends Message {

    private static final String MESSAGE_TYPE = "Proxy";

    private static final long serialVersionUID = 1L;

    private UUID messageId;

    private String proxyType;

    private String action;

    private String payload;

    public ProxyMessage() {
        this.setType(MESSAGE_TYPE);
        this.messageId = UUID.randomUUID();
    }

    public ProxyMessage(String messageId, String proxyType, String action, String payload) {
        this.setType(MESSAGE_TYPE);
        if (messageId != null) {
            this.messageId = UUID.fromString(messageId);
        }
        this.proxyType = proxyType;
        this.action = action;
        this.payload = payload;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
