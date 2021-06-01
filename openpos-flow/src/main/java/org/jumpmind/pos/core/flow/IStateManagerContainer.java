package org.jumpmind.pos.core.flow;

import java.util.Map;

public interface IStateManagerContainer {

    IStateManager create(String appId, String deviceId, Map<String, Object> queryParams, Map<String, String> personalizationProperties);
    
    IStateManager retrieve(String deviceId);
    
    void remove(String deviceId);
    
    void removeSessionIdVariables(String sessionId);
    
    void setCurrentStateManager(IStateManager stateManager);
    
    IStateManager getCurrentStateManager();
    
}
