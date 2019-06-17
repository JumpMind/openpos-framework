package org.jumpmind.pos.core.flow;

public interface ISessionListener {
    
    public void connected(String sessionId, StateManager stateManager);
    
    public void disconnected(String sessionId, StateManager stateManager);
    
}
