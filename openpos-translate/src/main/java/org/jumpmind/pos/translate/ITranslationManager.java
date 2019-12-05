package org.jumpmind.pos.translate;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.server.model.Action;

public interface ITranslationManager {

	public void init(Class<?> subsystemClass);
	
	public void setStateManager(IStateManager stateManager);
	
    public void setTranslationManagerSubscriber(ITranslationManagerSubscriber subscriber);
    
    public void doAction(String appId, Action action, Form formResults);
    
    public void showActiveScreen();
    
    public boolean processLegacyScreen(ILegacyScreen screen);
    
    public void executeMacro(InteractionMacro macro);
    
    public void sendAction(String action);

	public boolean showLegacyScreen(ILegacyScreen screen);
	    
}
