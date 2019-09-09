package org.jumpmind.pos.core.error;

import org.jumpmind.pos.core.flow.IStateManager;

public interface IErrorHandler {

    void handleError(IStateManager stateManager, Throwable throwable);
    
}
