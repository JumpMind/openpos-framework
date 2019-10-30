package org.jumpmind.pos.core.ui;

import lombok.*;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UIDataMessage<T> extends Message {
    private static final long serialVersionUID = 1L;

    private String dataType;
    private int seriesId;
    private T data;

    public UIDataMessage(String dataType) {
        this();
        this.dataType = dataType;
    }

    public UIDataMessage() {
        setType(MessageType.UIData);
    }

    @Override
    public String getType() {
        return MessageType.UIData;
    }
}
