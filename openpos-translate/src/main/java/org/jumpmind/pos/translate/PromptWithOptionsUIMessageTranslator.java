package org.jumpmind.pos.translate;

import org.jumpmind.pos.core.ui.OptionItem;
import org.jumpmind.pos.core.ui.message.PromptWithOptionsUIMessage;

import java.util.List;
import java.util.Properties;

/**
 * General purpose translator that results in rendering a screen with the OrPOS
 * prompt text, an input text field, and a list of mutually exclusive options
 * for the cashier to select.
 */
public class PromptWithOptionsUIMessageTranslator extends AbstractPromptUIMessageTranslator<PromptWithOptionsUIMessage> {

    public PromptWithOptionsUIMessageTranslator(ILegacyScreen legacyScreen, Class<PromptWithOptionsUIMessage> screenClass) {
        super(legacyScreen, screenClass);
    }
    
    public PromptWithOptionsUIMessageTranslator(ILegacyScreen legacyScreen, String appId, Properties properties) {
        super(legacyScreen, PromptWithOptionsUIMessage.class, appId, properties);
    }    

    @Override
    protected void buildMainContent() {
        super.buildMainContent();
        this.buildOptions();
        this.configureScreenResponseField();
        
    }
    
    protected void buildOptions() {
        List<OptionItem> options = generateUIActionsForLocalNavButtons(OptionItem.class, true);
        screen.setOptions(options);
    }
    
}
