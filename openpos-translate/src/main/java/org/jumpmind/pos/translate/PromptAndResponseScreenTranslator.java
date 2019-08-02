package org.jumpmind.pos.translate;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.screen.DialogProperties;
import org.jumpmind.pos.core.screen.IconType;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.template.SellTemplate;
import org.jumpmind.pos.server.model.Action;

public class PromptAndResponseScreenTranslator<T extends PromptScreen> extends AbstractPromptScreenTranslator<T> {

    private boolean addLocalMenuItems = false;
    private boolean showAsDialog = false;
    protected InteractionMacro undoMacro;

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
        if( showAsDialog ) {
            DialogProperties props = new DialogProperties();
            props.setMinWidth("50%");
            screen.asDialog(props);
            ActionItem back = (ActionItem)screen.get("backButton");
            if( back != null ) { 
                screen.addOtherAction( new ActionItem( back.getAction(), "Back"));
            }
        }
        if (addLocalMenuItems) {
            List<ActionItem> localNavButtons = generateUIActionsForLocalNavButtons(ActionItem.class, true);
            localNavButtons = localNavButtons.stream().filter(p -> !(p.getTitle().equals("Next"))).collect(Collectors.toList());
            if( showAsDialog ) {
                localNavButtons.forEach(b -> {
                    screen.addOtherAction(b);
                });
            } else {
                SellTemplate template = screen.getTemplate();
                template.setLocalMenuItems(localNavButtons);
            }
        }
        addActionButton();
    }
    
    protected void addActionButton() {
        screen.setActionButton(new ActionItem("Next", "Next", IconType.Forward));
    }
    
    public void setUndoMacro(InteractionMacro undoMacro) {
        this.undoMacro = undoMacro;
    }
    
    public void setShowAsDialog( boolean showAsDialog ) {
        this.showAsDialog = showAsDialog;
    }

    @Override
    public void handleAction(ITranslationManagerSubscriber subscriber, TranslationManagerServer tmServer, Action action,
            Form formResults) {
        if ("Undo".equals(action.getName()) && undoMacro != null) {
            tmServer.executeMacro(undoMacro);
        } else {
            super.handleAction(subscriber, tmServer, action, formResults);
        }
    }
}
