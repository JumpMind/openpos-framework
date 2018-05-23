package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.core.flow.ui.PromptConfig;

public interface IUI {

    public void notify(String message, String action);

    public void askYesNo(String message, String yesAction, String noAction);
    
    
    public void prompt(String message);
    public void prompt(PromptConfig promptConfig);
    
}
