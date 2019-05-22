package org.jumpmind.pos.core.clientconfiguration;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

import java.util.Map;

public class ClientConfigChangedMessage extends Message {

    private String configType;

    public ClientConfigChangedMessage(String configType, Map<String,String> configProps) {
        super(MessageType.ConfigChanged);
        if(configProps != null ) {
            configProps.forEach((propName, propValue) -> put(propName, propValue));
        }
        setConfigType(configType);
    }

    public ClientConfigChangedMessage(String name) {
        setConfigType(configType);
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }
}
