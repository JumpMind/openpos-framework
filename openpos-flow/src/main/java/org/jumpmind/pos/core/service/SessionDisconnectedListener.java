package org.jumpmind.pos.core.service;

import org.jumpmind.pos.core.flow.IStateManagerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectedListener implements ApplicationListener<SessionDisconnectEvent> {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IStateManagerContainer stateManagerContainer;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {        
        Message<?> msg = event.getMessage();
        String sessionId = (String) msg.getHeaders().get("simpSessionId");
        logger.info("session disconnected: {}", sessionId); 
        stateManagerContainer.removeSessionIdVariables(sessionId);
        stateManagerContainer.setCurrentStateManager(null);
    }

}