package org.jumpmind.pos.core.service;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerContainer;
import org.jumpmind.pos.core.model.ConfigChangedMessage;
import org.jumpmind.pos.core.model.IConfigSelector;
import org.jumpmind.pos.core.clientconfiguration.IClientConfigSelector;
import org.jumpmind.pos.core.clientconfiguration.ClientConfigChangedMessage;
import org.jumpmind.pos.core.clientconfiguration.ClientConfiguration;
import org.jumpmind.pos.core.screen.DialogProperties;
import org.jumpmind.pos.core.screen.DialogScreen;
import org.jumpmind.pos.core.screen.IconType;
import org.jumpmind.pos.server.config.MessageUtils;
import org.jumpmind.pos.server.config.PersonalizationParameters;
import org.jumpmind.pos.server.config.SessionSubscribedEvent;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.server.service.SessionConnectListener;
import org.jumpmind.pos.util.Versions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SessionSubscribedListener implements ApplicationListener<SessionSubscribedEvent>, MessageUtils {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    IStateManagerContainer stateManagerContainer;

    @Autowired
    IMessageService messageService;

    @Autowired
    SessionConnectListener sessionAuthTracker;

    @Value("${openpos.incompatible.version.message:The compatibility version of the client does not match the server}")
    String incompatibleVersionMessage; 
    
    @Autowired(required=false)
    PersonalizationParameters personalizationParameters;

    @Autowired
    ApplicationContext applicationContext;
    
    @Autowired
    Versions versions;

    @Override
    public void onApplicationEvent(SessionSubscribedEvent event) {
        Message<?> msg = event.getMessage();
        String sessionId = (String) msg.getHeaders().get("simpSessionId");
        Map<String, Object> queryParams = sessionAuthTracker.getQueryParams(sessionId);
        String topicName = (String) msg.getHeaders().get("simpDestination");
        String compatibilityVersion = this.getHeader(msg, MessageUtils.COMPATIBILITY_VERSION_HEADER);
        String deviceId = topicName.substring(topicName.indexOf("/node/") + "/node/".length());
        String appId = topicName.substring(topicName.indexOf("/app/") + "/app/".length(), topicName.indexOf("/node/"));
        Map<String, String> personalizationProperties = sessionAuthTracker.getPersonalizationResults(sessionId);

        try {
            log.info("session {} subscribed to {}", sessionId, topicName);
            IStateManager stateManager = stateManagerContainer.retrieve(appId, deviceId);
            boolean created = false;
            if (stateManager == null) {
                stateManager = stateManagerContainer.create(appId, deviceId, queryParams, personalizationProperties);
                created = true;
            } else {
                stateManager.registerQueryParams(queryParams);
                stateManager.registerPersonalizationProperties(personalizationProperties);
                stateManager.sendConfigurationChangedMessage();
            }

            stateManagerContainer.setCurrentStateManager(stateManager);

            stateManager.setSessionAuthenticated(sessionId, sessionAuthTracker.isSessionAuthenticated(sessionId));
            stateManager.setSessionCompatible(sessionId, sessionAuthTracker.isSessionCompatible(sessionId));

            if (!stateManager.isSessionAuthenticated(sessionId)) {
                DialogScreen errorDialog = new DialogScreen();
                errorDialog.asDialog(new DialogProperties(false));
                errorDialog.setIcon(IconType.Error);
                errorDialog.setTitle("Failed Authentication");
                errorDialog.setMessage(Arrays.asList("The client and server authentication tokens did not match"));
                messageService.sendMessage(appId, deviceId, errorDialog);
            } else if (!stateManager.isSessionCompatible(sessionId)) {
                log.warn("Client compatiblity version of '{}' for deviceId '{}' is not compatible with the server", compatibilityVersion,
                        deviceId);
                DialogScreen errorDialog = new DialogScreen();
                // If there is no compatibility version, the client is an older
                // client that used the type attribute
                // instead of the screenType attribute for the screen type
                // value. In that case need to set the type attribute or
                // the dialog will not display on older clients
                if (compatibilityVersion == null) {
                    errorDialog.setType(errorDialog.getScreenType());
                }
                errorDialog.asDialog(new DialogProperties(false));
                errorDialog.setIcon(IconType.Error);
                errorDialog.setTitle("Incompatible Versions");
                errorDialog.setMessage(Arrays.asList(incompatibleVersionMessage.split("\n")));
                messageService.sendMessage(appId, deviceId, errorDialog);
            } else {
                if (!created) {
                    stateManager.refreshScreen();
                }
            }

        } catch (Exception ex) {
            log.error("Failed to subscribe to " + topicName, ex);
            DialogScreen errorDialog = new DialogScreen();
            errorDialog.asDialog(new DialogProperties(false));
            errorDialog.setIcon(IconType.Error);
            errorDialog.setTitle("Failed To Subscribe");
            errorDialog.setMessage(Arrays.asList(ex.getMessage()));
            messageService.sendMessage(appId, deviceId, errorDialog);
        } finally {
            stateManagerContainer.setCurrentStateManager(null);
        }
    }
}
