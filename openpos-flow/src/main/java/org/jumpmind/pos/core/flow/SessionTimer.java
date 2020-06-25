package org.jumpmind.pos.core.flow;

import java.util.Timer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionTimer {

    private Timer timer;

    @Autowired
    private StateManagerContainer stateManagerContainer;
    
    private static final long TIMEOUT_CHECK_INTERVAL = 1000;
        
    public static final String ACTION_KEEP_ALIVE = "KeepAlive";

    @Scheduled(fixedRate = TIMEOUT_CHECK_INTERVAL)
    protected void checkSessionAges() {
        if (stateManagerContainer == null) {
            throw new FlowException("stateManagerFactory cannot be null.");
        }
        log.trace("checking if StateManagers need to time out due to inactivity");
        for (StateManager stateManager : stateManagerContainer.getAllStateManagers()) {
            stateManagerContainer.setCurrentStateManager(stateManager);
            stateManager.checkSessionTimeout();
        }
    }
}
