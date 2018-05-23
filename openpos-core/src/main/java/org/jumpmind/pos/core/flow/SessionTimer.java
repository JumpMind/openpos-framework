package org.jumpmind.pos.core.flow;

import java.util.Timer;

import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionTimer {
    
    private Timer timer;
    
    private static final Logger log = Logger.getLogger(SessionTimer.class);
    
    @Autowired
    private StateManagerFactory stateManagerFactory;
    
    private static long ONE_MINUTE = 60000;
        
    public static final String ACTION_KEEP_ALIVE = "KeepAlive";
    
    @PostConstruct
    public void start() {
        timer = new Timer("SessionTimer", true);
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkSessionAges();
            }
        }, ONE_MINUTE, ONE_MINUTE);
    }
    
    public void stop() {
        timer.cancel();
    }
    
    protected void checkSessionAges() {
        if (stateManagerFactory == null) {
            throw new FlowException("stateManagerFactory cannot be null.");
        }
        log.debug("checking if StateManagers need to time out due to inactivity.");
        for (StateManager stateManager : stateManagerFactory.getAllStateManagers()) {
            stateManager.checkSessionTimeout();
        }
    }    
}
