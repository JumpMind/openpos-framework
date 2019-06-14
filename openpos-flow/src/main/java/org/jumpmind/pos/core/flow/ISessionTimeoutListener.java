package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.server.model.Action;

public interface ISessionTimeoutListener {
    
    public void onSessionTimeout(StateManager stateManager, Action action);
    
}
