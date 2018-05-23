package org.jumpmind.pos.translate;

public interface ILegacySubsystem {

    public ILegacyScreen getActiveScreen();
    
    public void sendAction(String action);
    
    public void sendAction(String action, int number);
    
    public void setLegacyScreenListener(ITranslationManager listener);
}
