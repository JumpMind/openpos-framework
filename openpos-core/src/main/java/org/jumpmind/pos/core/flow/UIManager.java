package org.jumpmind.pos.core.flow;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.ui.PromptConfig;
import org.jumpmind.pos.core.screen.DialogScreen;
import org.jumpmind.pos.core.screen.MenuItem;
import org.jumpmind.pos.core.screen.PromptScreen;
import org.jumpmind.pos.core.screen.Toast;
import org.jumpmind.pos.core.screen.ToastType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_PROTOTYPE)
public class UIManager implements IUI {
    
    private IStateManager stateManager;

    protected void init(IStateManager stateManager) {
        this.stateManager = stateManager;
    }
    
    @Override
    public void notify(String message, ToastType toastType, int duration) {        
        stateManager.showToast(new Toast(message, toastType, duration*1000, "top"));
//        DialogScreen screen = new DialogScreen();
//        screen.asDialog(new DialogProperties(true));
//        screen.setTitle(message);
//        screen.setRefreshAlways(true);
//        stateManager.showScreen(screen);  
    }
    
    @Override
    public void prompt(String message, String action) {
        DialogScreen screen = new DialogScreen();
        screen.setTitle(message);
        screen.setRefreshAlways(true);
        screen.setButtons(Arrays.asList(new MenuItem("Ok", action, true) ));
        stateManager.showScreen(screen);        
    }
    
    @Override
    public void askYesNo(String message, String yesAction, String noAction) {
        DialogScreen screen = new DialogScreen();
        screen.setTitle(message);
        screen.setRefreshAlways(true);
        screen.setButtons(Arrays.asList(new MenuItem("Yes", yesAction, true), new MenuItem("No", noAction, true) ));
        stateManager.showScreen(screen);        
    }

    @Override
    public void prompt(PromptConfig promptConfig) {
        this.prompt(promptConfig, false);
    }
    
    @Override
    public void prompt(PromptConfig promptConfig, boolean isDialog) {
        PromptScreen screen = new PromptScreen();

        if (promptConfig.getName() != null) {
            screen.setName(promptConfig.getName());
        } else if (promptConfig.getPlaceholder() != null) {
            screen.setName(promptConfig.getPlaceholder());
        } else {
            screen.setName("Prompt " + StringUtils.abbreviate(promptConfig.getPromptText(), 25));
        }

        screen.setOtherActions(promptConfig.getOtherActions());
        screen.setRefreshAlways(true);
        screen.setResponseType(promptConfig.getPromptType());
        if (promptConfig.getBackAction() != null) {
            screen.setBackButton(new MenuItem(promptConfig.getBackAction(), "Back"));
        }
        screen.setPromptIcon(promptConfig.getIcon());
        screen.setIcon(promptConfig.getIcon());
        screen.setPlaceholderText(promptConfig.getPlaceholder());
        screen.setText(promptConfig.getPromptText());
        if (promptConfig.getActionMenuItem() != null) {
            screen.setActionButton(promptConfig.getActionMenuItem());
        } else {
            screen.setActionButton(new MenuItem("Next", "UsernameEntered", true));
        }

        if (isDialog) {
            if (promptConfig.getDialogProperties() != null) {
                screen.setDialogProperties(promptConfig.getDialogProperties());
            }
            stateManager.showScreen(screen.asDialog());
        } else {
            stateManager.showScreen(screen);
        }

    }

}
