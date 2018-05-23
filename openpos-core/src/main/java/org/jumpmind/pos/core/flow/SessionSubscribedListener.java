package org.jumpmind.pos.core.flow;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SessionSubscribedListener implements ApplicationListener<SessionSubscribeEvent> {

    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    @Qualifier("clientOutboundChannel")
    private MessageChannel clientOutboundChannel;
    
    @Autowired
    IStateManagerFactory stateManagerFactory;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        String topicName = (String) event.getMessage().getHeaders().get("simpDestination");
        String nodeId = topicName.substring(topicName.indexOf("/node/") + "/node/".length());
        String appId = topicName.substring(topicName.indexOf("/app/") + "/app/".length(), topicName.indexOf("/node/"));
        logger.info("subscribed to {}", topicName);
        IStateManager stateManager = stateManagerFactory.retrieve(appId, nodeId);
        try {
            if (stateManager == null) {
                stateManager = stateManagerFactory.create(appId, nodeId);
            } 
            stateManager.refreshScreen();
        } catch (Exception ex) {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
            headerAccessor.setMessage(String.format("[%s] %s; Is %s a valid topic/app/node?", ex.getClass().getName(), ex.getMessage(), topicName));
            headerAccessor.setSessionId((String)event.getMessage().getHeaders().get("simpSessionId"));
            this.clientOutboundChannel.send(MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders()));
            throw ex;
        }
    }

}