package org.jumpmind.pos.translate;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.screen.ChooseOptionsScreen;
import org.jumpmind.pos.core.screen.OptionItem;
import org.jumpmind.pos.core.screen.ScreenType;

public class ChooseOptionsScreenTranslator<T extends ChooseOptionsScreen> extends AbstractLegacyScreenTranslator<T> { 
    
    protected Function<OptionItem, Boolean> optionItemEvalFunc = null;
    protected InteractionMacro undoMacro;

    public ChooseOptionsScreenTranslator(ILegacyScreen headlessScreen, Class<T> screenClass) {
        this(headlessScreen, screenClass, null);
    }

    /**
     * Constructor to be used for modifying OptionItems or excluding them.
     *  
     * @param headlessScreen The legacy screen,
     * @param screenClass The screenClass
     * @param optionFilter A function that can be used for modifying options or excluding them.  The given function will be invoked
     * for each OptionItem.  If the function returns false, the item will be excluded.
     */
    public ChooseOptionsScreenTranslator(ILegacyScreen headlessScreen, Class<T> screenClass, Function<OptionItem, Boolean> optionFilter) {
        super(headlessScreen, screenClass);
        this.optionItemEvalFunc = optionFilter;
        screen.setType(ScreenType.ChooseOptions);
    }
    
    public void setUndoMacro(InteractionMacro undoMacro) {
        this.undoMacro = undoMacro;
    }
    
    @Override
    protected void buildMainContent() {
        super.buildMainContent();
        this.buildOptions();
       
        String formattedPromptText = this.getPromptText(this.getLegacyUIModel(), this.getLegacyAssignmentSpec(PROMPT_RESPONSE_PANEL_KEY), 
                legacyScreen.getResourceBundleFilename()).orElse(null);
        screen.setPromptText(formattedPromptText);
    }
    
    protected void buildOptions() {
        List<OptionItem> options = this.generateUIActionsForLocalNavButtons(OptionItem.class, true, new String[]{});
        if (this.optionItemEvalFunc != null) {
            options = options.stream().filter(o -> { return this.optionItemEvalFunc.apply(o); }).collect(Collectors.toList());
        }
        screen.setOptions(options);
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
