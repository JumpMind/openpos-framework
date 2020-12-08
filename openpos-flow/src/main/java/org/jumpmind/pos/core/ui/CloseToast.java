package org.jumpmind.pos.core.ui;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

import java.util.UUID;

public class CloseToast extends Message {
    private UUID persistedId;

    public CloseToast(UUID persistedId) {
        this.persistedId = persistedId;
        setType(MessageType.CloseToast);
        setWillUnblock(true);
    }

    public UUID getPersistedId() {
        return persistedId;
    }

    public void setPersistedId(UUID persistedId) {
        this.persistedId = persistedId;
    }
}
