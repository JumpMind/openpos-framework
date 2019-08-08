package org.jumpmind.pos.translate;

import org.jumpmind.pos.core.ui.message.LoadingDialogUIMessage;
import org.jumpmind.pos.core.ui.messagepart.DialogHeaderPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;

public class StatusUIMessageTranslator extends AbstractLegacyUIMessageTranslator<LoadingDialogUIMessage> {
    public enum Mode {
        /** Displays the given title and message or uses values from legacy resources if title and message not given. */
        Default,
        /** Moves the message text to the title area and eliminates the message text. */
        UseMessageForTitle 
    }
    
    private Mode mode = Mode.Default;
    private String title;
    
    public StatusUIMessageTranslator(ILegacyScreen legacyScreen) {
        this(legacyScreen, Mode.Default);
    }

    public StatusUIMessageTranslator(ILegacyScreen legacyScreen, Mode mode) {
        this(legacyScreen, (String) null);
        this.mode = mode;
    }
    
    public StatusUIMessageTranslator(ILegacyScreen legacyScreen, String title) {
        this(legacyScreen, title, (String)null);
    }

    public StatusUIMessageTranslator(ILegacyScreen legacyScreen, String title, String message) {
        super(legacyScreen, LoadingDialogUIMessage.class);
        this.title = title;
        getScreen().setMessage(message);
    }

    @Override
    public void buildMainContent() {
       super.buildMainContent();
        DialogHeaderPart dialogHeaderPart = new DialogHeaderPart();
        this.screen.addMessagePart(MessagePartConstants.DialogHeader, dialogHeaderPart);

       
       String statusText = this.getPromptText(this.getLegacyUIModel(), this.getLegacyAssignmentSpec(PROMPT_RESPONSE_PANEL_KEY), 
               this.getResourceBundleFilename()).orElse(null);
       
       if (getMode() == Mode.UseMessageForTitle) {
            title = this.hasMessage() ? getScreen().getMessage() : statusText;
           getScreen().setMessage((String) null);
       } else { // Default
           
           if (title == null) {
               title = this.getScreenName();
           }
           
           // If a message hasn't already been set, just use what might be available as the Prompt text
           if (! this.hasMessage()) {
               getScreen().setMessage(statusText);
           }
       }

        dialogHeaderPart.setHeaderText(title);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
    
    protected boolean hasTitle() {
        return title != null;
    }
    
    protected boolean hasMessage() {
        return getScreen().getMessage() != null;
    }
}
