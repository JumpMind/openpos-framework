package org.jumpmind.pos.core.flow;

import java.util.Timer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SessionTimer {

    private Timer timer;

    private static final Logger log = Logger.getLogger(SessionTimer.class);

    @Autowired
    private StateManagerContainer stateManagerContainer;

    private static final long TIMEOUT_CHECK_INTERVAL = 1000;

    public static final String ACTION_KEEP_ALIVE = "KeepAlive";

    @Scheduled(fixedRate = TIMEOUT_CHECK_INTERVAL)
    protected void checkSessionAges() {
        if (stateManagerContainer == null) {
            throw new FlowException("stateManagerFactory cannot be null.");
        }
        log.debug("checking if StateManagers need to time out due to inactivity");
        for (StateManager stateManager : stateManagerContainer.getAllStateManagers()) {
            stateManager.checkSessionTimeout();
        }
    }
}
