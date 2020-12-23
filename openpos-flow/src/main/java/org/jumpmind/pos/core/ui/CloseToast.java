package org.jumpmind.pos.core.ui;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

import java.util.UUID;

public class CloseToast extends Message {
    private String persistedId;

    public CloseToast(String persistedId) {
        this.persistedId = persistedId;
        setType(MessageType.CloseToast);
        setWillUnblock(true);
    }

    public String getPersistedId() {
        return persistedId;
    }

    public void setPersistedId(String persistedId) {
        this.persistedId = persistedId;
    }
}
