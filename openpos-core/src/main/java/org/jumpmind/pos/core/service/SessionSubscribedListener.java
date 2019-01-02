package org.jumpmind.pos.core.service;

import java.util.Arrays;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerFactory;
import org.jumpmind.pos.core.screen.DialogProperties;
import org.jumpmind.pos.core.screen.DialogScreen;
import org.jumpmind.pos.core.screen.IconType;
import org.jumpmind.pos.server.config.MessageUtils;
import org.jumpmind.pos.server.config.SessionSubscribedEvent;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.server.service.SessionConnectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SessionSubscribedListener implements ApplicationListener<SessionSubscribedEvent>, MessageUtils {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IStateManagerFactory stateManagerFactory;

    @Autowired
    IMessageService messageService;
    
    @Autowired
    SessionConnectListener sessionAuthTracker;
    
    @Value("${openpos.incompatible.version.message:The compatibility version of the client does not match the server}")
    String incompatibleVersionMessage; 

    @Override
    public void onApplicationEvent(SessionSubscribedEvent event) {
        Message<?> msg = event.getMessage();
        String sessionId = (String) msg.getHeaders().get("simpSessionId");
        String topicName = (String) msg.getHeaders().get("simpDestination");
        String compatibilityVersion = this.getHeader(msg, MessageUtils.COMPATIBILITY_VERSION_HEADER);
        String nodeId = topicName.substring(topicName.indexOf("/node/") + "/node/".length());
        String appId = topicName.substring(topicName.indexOf("/app/") + "/app/".length(), topicName.indexOf("/node/"));
        try {
            logger.info("session {} subscribed to {}", sessionId, topicName);
            IStateManager stateManager = stateManagerFactory.retrieve(appId, nodeId);
            if (stateManager == null) {
                stateManager = stateManagerFactory.create(appId, nodeId);
            }

            stateManager.setSessionAuthenticated(sessionId, sessionAuthTracker.isSessionAuthenticated(sessionId));
            stateManager.setSessionCompatible(sessionId, sessionAuthTracker.isSessionCompatible(sessionId));

            if (!stateManager.isSessionAuthenticated(sessionId)) {
                DialogScreen errorDialog = new DialogScreen();
                errorDialog.asDialog(new DialogProperties(false));
                errorDialog.setIcon(IconType.Error);
                errorDialog.setTitle("Failed Authentication");
                errorDialog.setMessage(Arrays.asList("The client and server authentication tokens did not match"));
                messageService.sendMessage(appId, nodeId, errorDialog);
            } else if (!stateManager.isSessionCompatible(sessionId)) {
                logger.warn("Client compatiblity version of '{}' for nodeId '{}' is not compatible with the server", compatibilityVersion, nodeId);
                DialogScreen errorDialog = new DialogScreen();
                // If there is no compatibility version, the client is an older client that used the type attribute
                // instead of the screenType attribute for the screen type value. In that case need to set the type attribute or
                // the dialog will not display on older clients
                if (compatibilityVersion == null) {
                    errorDialog.setType(errorDialog.getScreenType());
                }
                errorDialog.asDialog(new DialogProperties(false));
                errorDialog.setIcon(IconType.Error);
                errorDialog.setTitle("Incompatible Versions");
                errorDialog.setMessage(Arrays.asList(incompatibleVersionMessage.split("\n")));
                messageService.sendMessage(appId, nodeId, errorDialog);
            } else {
                stateManager.refreshScreen();
            }

        } catch (Exception ex) {
            logger.error("Failed to subscribe to " + topicName, ex);
            DialogScreen errorDialog = new DialogScreen();
            errorDialog.asDialog(new DialogProperties(false));
            errorDialog.setIcon(IconType.Error);
            errorDialog.setTitle("Failed To Subscribe");
            errorDialog.setMessage(Arrays.asList(ex.getMessage()));
            messageService.sendMessage(appId, nodeId, errorDialog);
        }
    }

}