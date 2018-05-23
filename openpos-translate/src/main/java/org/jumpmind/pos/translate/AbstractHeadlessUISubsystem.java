package org.jumpmind.pos.translate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHeadlessUISubsystem implements ILegacySubsystem {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected final static String MOBILE_POS_SCREEN_PREFIX = "MobilePOS_";    

    protected String configFileName;
    protected String uiPropertyFile;
    protected String factoryName;
    protected ITranslationManager translationManager;
    protected boolean statusUpdating = false;  // indicates if the current showScreen call is to 'SHOW_STATUS_ONLY'
    
    
    public void setLegacyScreenListener(ITranslationManager listener) {
        this.translationManager = listener;
    }

    public void setConfigFilename(String name) {
        this.configFileName = name;
    }

    public String getFactoryClassName() {
        return factoryName;
    }

    public String getUiPropertyFile() {
        return uiPropertyFile;
    }
    
    public void setUiPropertyFile(String uiPropertyFile) {
        this.uiPropertyFile = uiPropertyFile;
    }

    public void setFactoryClassName(String factoryClassName) {
        this.factoryName = factoryClassName;
    }
    
    public boolean hasConnections(String screen) {
        return false;
    }
    
    public boolean isStatusUpdating() {
        return statusUpdating;
    }

    public void setStatusUpdating(boolean statusUpdating) {
        this.statusUpdating = statusUpdating;
    }
    
    /**
     * Indicates if the screen exists only in the POS Mobile framework
     * and is not an OrPOS screen with a corresponding resource configuration.
     */
    protected boolean isScreenMobileOnly(String screenId) {
        return screenId != null && screenId.startsWith(MOBILE_POS_SCREEN_PREFIX);
    }
    
    abstract public ILegacyScreen getActiveScreen();
    
}
