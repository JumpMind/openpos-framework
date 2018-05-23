package org.jumpmind.pos.translate;

public interface ILegacyScreen {

    public String getSpecName();
    
    public <T> T getCargo();
    
    public <T> T getBeanSpec(String specName);
    
    public <T> T getOverlaySpec();
    
    public <T> T getBus();
    
    public <T> T getModel();
    
    public String getResourceBundleFilename();
    
    public String getDialogResourceId();
    
    public <T> T getAsgnmntSpec(String panelKey);
    
    public boolean isStatusUpdate();
    
    public boolean isDialog();
    
    public ITranslationManager getTranslationManager();
    
}
