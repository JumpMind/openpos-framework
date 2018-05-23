package org.jumpmind.pos.translate;

public interface ILegacyStartupService {

    public void startPreviouslyStarted();
    
    public void start(String nodeId);
    public boolean restart(String nodeId);
    
    public ITranslationManager getTranslationManagerRef(String nodeId);
    
}
