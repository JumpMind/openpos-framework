package org.jumpmind.pos.core.error;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.OnGlobalAction;
import org.jumpmind.pos.server.model.Action;
import org.springframework.beans.factory.annotation.Autowired;

public class GlobalErrorActionHandler {

    @Autowired
    IErrorHandler errorHandler;

    @Autowired
    IStateManager stateManager;

    @OnGlobalAction
    public void onGlobalError(Action action){
        Throwable t = null;
        if( action.getData() instanceof Throwable){
            t = Action.convertActionData(action.getData(), Throwable.class);

        }
        errorHandler.handleError(stateManager, t);
    }
}
