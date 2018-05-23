package org.jumpmind.pos.translate;

import java.util.List;
import java.util.Properties;

import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.screen.MenuItem;
import org.jumpmind.pos.core.screen.PromptScreen;
import org.jumpmind.pos.core.template.SellTemplate;

public class PromptAndResponseScreenTranslator<T extends PromptScreen> extends AbstractPromptScreenTranslator<T> {

    private boolean addLocalMenuItems = false;

    public PromptAndResponseScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass, boolean addLocalMenuItems) {
        this(legacyScreen, screenClass, addLocalMenuItems, (FieldInputType) null);
    }

    public PromptAndResponseScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass, boolean addLocalMenuItems, String appId, Properties properties) {
        this(legacyScreen, screenClass, addLocalMenuItems, (FieldInputType) null, null, null, appId, properties);
    }   
    
    public PromptAndResponseScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass, boolean addLocalMenuItems,
            FieldInputType responseType) {
        this(legacyScreen, screenClass, addLocalMenuItems, responseType, null, null);
    }

    public PromptAndResponseScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass, boolean addLocalMenuItems,
            FieldInputType responseType, Integer minLength, Integer maxLength) {
        this(legacyScreen, screenClass, addLocalMenuItems, responseType, minLength, maxLength, null, null);
    }
    

    public PromptAndResponseScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass, boolean addLocalMenuItems,
            FieldInputType responseType, Integer minLength, Integer maxLength, String appId, Properties properties) {
        super(legacyScreen, screenClass, appId, properties);
        screen.setTemplate(new SellTemplate());
        this.addLocalMenuItems = addLocalMenuItems;
        screen.setResponseType(responseType != null ? responseType.name() : null);
        screen.setMinLength(minLength);
        screen.setMaxLength(maxLength);
    }   
    
    @Override
    protected void buildMainContent() {
        super.buildMainContent();
        screen.setRefreshAlways(true);
        this.configureScreenResponseField();
        if (addLocalMenuItems) {
            List<MenuItem> localNavButtons = generateUIActionsForLocalNavButtons(MenuItem.class, true);
            SellTemplate template = screen.getTemplate();
            template.setLocalMenuItems(localNavButtons);
        }
        addActionButton();
    }
    
    protected void addActionButton() {
        screen.setActionButton(new MenuItem("Next", "Next", "keyboard_arrow_right"));
    }

}
