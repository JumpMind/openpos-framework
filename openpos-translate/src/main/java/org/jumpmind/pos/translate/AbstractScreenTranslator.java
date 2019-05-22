package org.jumpmind.pos.translate;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.POSSessionInfo;
import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractScreenTranslator<T extends Screen> implements ITranslator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ILegacyScreen legacyScreen;

    protected T screen;

    protected POSSessionInfo posSessionInfo;

    protected IScreenThemeSelector screenThemeSelector;

    protected Map<String, String> iconRegistry = new HashMap<>();
    
    protected Class<T> screenClass;
    
    protected String appId;
    
    protected Properties properties;

    public AbstractScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass) {
        this.legacyScreen = legacyScreen;
        this.screenClass = screenClass;
        newScreen();
    }
    
    protected void newScreen() {
        if (screenClass == null) {
            throw new RuntimeException("screenClass cannot be null at this point. Legacy screen: " + this.getLegacyScreen());
        }            
        try {
            screen = screenClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getIconRegistry() {
        return this.iconRegistry;
    }
    
    public void setIconRegistry(Map<String, String> iconRegistry) {
        this.iconRegistry = iconRegistry;
    }

    public void setScreenThemeSelector(IScreenThemeSelector screenThemeSelector) {
        this.screenThemeSelector = screenThemeSelector;
    }

    protected void chooseScreenTheme() {
        if (this.screenThemeSelector != null) {
            boolean trainingOn = posSessionInfo.getTrainingMode();

        }
    }

    protected void chooseScreenName() {
        if (getScreen().getName() == null) {
            getScreen().setName(legacyScreen.getSpecName());
        }
    }

    public T build() {
        logger.info("{} is building a screen of type '{}'", getClass().getSimpleName(), getScreen().getScreenType());
        if (isBlank(screen.getIcon())) {
            screen.setIcon(iconRegistry.get(screen.getName()));
        }
        chooseLocale();
        chooseScreenName();
        updatePosSessionInfo();
        chooseScreenTheme();
        buildMainContent();
        return screen;
    }

    public ILegacyScreen getLegacyScreen() {
        return this.legacyScreen;
    }

    abstract protected void buildMainContent();

    abstract protected void updatePosSessionInfo();

    protected T getScreen() {
        return screen;
    }

    protected void chooseLocale() {
        getScreen().setLocale(Locale.getDefault().toLanguageTag());
    }
    
    public POSSessionInfo getPOSSessionInfo() {
        return this.posSessionInfo;
    }
    
    public void setPosSessionInfo(POSSessionInfo posSessionInfo) {
        this.posSessionInfo = posSessionInfo;
    }

    protected void resetScreen() {
        this.getScreen().clearAdditionalProperties();
    }

    public void handleAction(ITranslationManagerSubscriber subscriber, TranslationManagerServer tmServer, Action action,
            Form formResults) {
        tmServer.sendAction(action.getName());
    } 
    
    public void setBackButton(String action) {
    		this.screen.setBackButton(new ActionItem("Back", action, true));
    }

    public void setAppId(String appId) {
    	this.appId = appId;
    }
    
    public void setProperties(Properties properties) {
    	this.properties = properties;
    }
    
}
