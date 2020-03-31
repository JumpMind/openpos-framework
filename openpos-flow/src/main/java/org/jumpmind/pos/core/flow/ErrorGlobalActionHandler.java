package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.server.model.Action;

public class ErrorGlobalActionHandler {

    public static final String RESET_STATE_MANAGER = "ResetStateManager";

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @OnGlobalAction
    public void onResetStateManager(Action action) {
        stateManager.reset();
    }
}
