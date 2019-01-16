package org.jumpmind.pos.core.model;

import org.jumpmind.pos.util.model.Message;

public class ConfigChangedMessage extends Message {

    private static final long serialVersionUID = 1L;

    private String theme;

    private ClientConfiguration configuration;

    public ConfigChangedMessage(String theme, ClientConfiguration configuration) {
        super(MessageType.ConfigChanged);
        this.theme = theme;
        this.configuration = configuration;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public ClientConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

}
