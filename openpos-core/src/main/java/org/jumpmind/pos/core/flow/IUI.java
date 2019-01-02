package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.core.flow.ui.PromptConfig;
import org.jumpmind.pos.core.screen.ToastType;

public interface IUI {

    public void notify(String message, ToastType type, int duration);
    public void prompt(String message, String action);
    public void askYesNo(String message, String yesAction, String noAction);       
    public void prompt(PromptConfig promptConfig);
    public void prompt(PromptConfig promptConfig, boolean isDialog);
    
}
