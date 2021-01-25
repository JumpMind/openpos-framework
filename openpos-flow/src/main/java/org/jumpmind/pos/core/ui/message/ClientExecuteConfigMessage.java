package org.jumpmind.pos.core.ui.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

@Data
@Builder
@AllArgsConstructor
public class ClientExecuteConfigMessage extends Message {
    private ClientExecutableType executableType;
    private String data;
    private String[] args;

    public ClientExecuteConfigMessage() {
        setType(MessageType.ClientExecute);
    }

    public enum ClientExecutableType {
        Command,
        File
    }
}
