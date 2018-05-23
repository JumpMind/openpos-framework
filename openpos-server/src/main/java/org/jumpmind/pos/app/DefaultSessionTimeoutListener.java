package org.jumpmind.pos.app;

import org.jumpmind.pos.core.flow.ISessionTimeoutListener;
import org.jumpmind.pos.core.flow.StateManager;
import org.springframework.stereotype.Component;

@Component
public class DefaultSessionTimeoutListener implements ISessionTimeoutListener {

    @Override
    public void onSessionTimeout(StateManager stateManager) {
        stateManager.endConversation();
        stateManager.endSession();
    }
}
