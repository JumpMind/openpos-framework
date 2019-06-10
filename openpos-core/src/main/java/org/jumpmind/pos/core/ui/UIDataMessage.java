package org.jumpmind.pos.core.ui;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

public class UIDataMessage extends Message {
    private static final long serialVersionUID = 1L;

    private String dataType;

    public UIDataMessage(String dataType) {
        this();
        this.dataType = dataType;
    }

    public UIDataMessage() {
        setType(MessageType.UIData);
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
