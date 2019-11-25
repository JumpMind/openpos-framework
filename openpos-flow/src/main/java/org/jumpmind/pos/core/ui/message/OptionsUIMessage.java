package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.OptionsListPart;

public class OptionsUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private String title;

    private String prompt;

    private String imageUrl;

    public OptionsUIMessage() {
        setScreenType(UIMessageType.OPTIONS);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
