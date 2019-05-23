package org.jumpmind.pos.translate;

import java.io.Serializable;
import java.util.Properties;

import org.jumpmind.pos.core.device.IDeviceMessageDispatcher;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.model.Action;

public interface ITranslationManagerSubscriber extends IDeviceMessageDispatcher, Serializable {

    public void showScreen(UIMessage screen);

    public void doAction(Action action);
    
    public boolean isInTranslateState();

    public String getNodeId();
    
    public String getAppId();
    
    public Properties getProperties();

    public void addClientConfigurationTag(String tag);
    public void removeClientConfigurationTag(String tag);

}
