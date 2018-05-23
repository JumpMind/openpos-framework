package org.jumpmind.pos.translate;

import java.util.List;
import java.util.Properties;

import org.jumpmind.pos.core.screen.OptionItem;
import org.jumpmind.pos.core.screen.PromptWithOptionsScreen;

/**
 * General purpose translator that results in rendering a screen with the OrPOS
 * prompt text, an input text field, and a list of mutually exclusive options
 * for the cashier to select.
 */
public class PromptWithOptionsScreenTranslator extends AbstractPromptScreenTranslator<PromptWithOptionsScreen> {

    public PromptWithOptionsScreenTranslator(ILegacyScreen legacyScreen, Class<PromptWithOptionsScreen> screenClass) {
        super(legacyScreen, screenClass);
    }
    
    public PromptWithOptionsScreenTranslator(ILegacyScreen legacyScreen, String appId, Properties properties) {
        super(legacyScreen, PromptWithOptionsScreen.class, appId, properties);
    }    

    @Override
    protected void buildMainContent() {
        super.buildMainContent();
        this.buildOptions();
        this.configureScreenResponseField();
        screen.setPrompt(screen.getText());
    }

    protected void buildOptions() {
        List<OptionItem> options = generateUIActionsForLocalNavButtons(OptionItem.class, true);
        screen.setOptions(options);
    }
    
    protected void addActionButton() {
        
    }
}
