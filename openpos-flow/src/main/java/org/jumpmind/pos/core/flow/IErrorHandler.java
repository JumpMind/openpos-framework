package org.jumpmind.pos.core.flow;

public interface IErrorHandler {

    public void handleError(IStateManager stateManager, Throwable throwable);
    
}
