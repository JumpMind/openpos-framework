package org.jumpmind.pos.core.service;

import org.jumpmind.pos.core.model.StartupMessage;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.util.event.DeviceConnectedEvent;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerContainer;
import org.jumpmind.pos.core.ui.DialogProperties;
import org.jumpmind.pos.core.ui.IconType;
import org.jumpmind.pos.core.ui.message.DialogUIMessage;
import org.jumpmind.pos.core.ui.messagepart.DialogHeaderPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;
import org.jumpmind.pos.server.config.MessageUtils;
import org.jumpmind.pos.devices.service.model.PersonalizationParameters;
import org.jumpmind.pos.server.config.SessionSubscribedEvent;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.server.service.SessionConnectListener;
import org.jumpmind.pos.util.Versions;
import org.jumpmind.pos.util.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

import static org.jumpmind.pos.util.AppUtils.setupLogging;

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

    @Autowired(required = false)
    PersonalizationParameters personalizationParameters;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Versions versions;

    @Autowired
    EventPublisher eventPublisher;


    @Override
    public void onApplicationEvent(SessionSubscribedEvent event) {
        Message<?> msg = event.getMessage();
        String sessionId = (String) msg.getHeaders().get("simpSessionId");
        Map<String, Object> queryParams = sessionAuthTracker.getQueryParams(sessionId);
        Map<String, String> clientContext = sessionAuthTracker.getClientContext(sessionId);
        String topicName = (String) msg.getHeaders().get("simpDestination");
        String compatibilityVersion = this.getHeader(msg, MessageUtils.COMPATIBILITY_VERSION_HEADER);
        String deviceId = topicName.substring(topicName.indexOf("/device/") + "/device/".length());
        setupLogging(deviceId);
        Map<String, String> personalizationProperties = sessionAuthTracker.getPersonalizationResults(sessionId);

        try {
            log.info("session {} subscribed to {}", sessionId, topicName);
            if (!sessionAuthTracker.isSessionAuthenticated(sessionId)) {
                DialogUIMessage errorDialog = new DialogUIMessage();
                DialogHeaderPart header = new DialogHeaderPart();
                errorDialog.asDialog(new DialogProperties(false));
                header.setHeaderIcon(IconType.Error);
                header.setHeaderText("Failed Authentication");
                errorDialog.addMessagePart(MessagePartConstants.DialogHeader, header);
                errorDialog.setMessage(Arrays.asList("The client and server authentication tokens did not match"));
                messageService.sendMessage(deviceId, errorDialog);
                return;
            } else if (!sessionAuthTracker.isSessionCompatible(sessionId)) {
                log.warn("Client compatibility version of '{}' for deviceId '{}' is not compatible with the server", compatibilityVersion,
                        deviceId);
                DialogUIMessage errorDialog = new DialogUIMessage();
                // If there is no compatibility version, the client is an older
                // client that used the type attribute
                // instead of the screenType attribute for the screen type
                // value. In that case need to set the type attribute or
                // the dialog will not display on older clients
                if (compatibilityVersion == null) {
                    errorDialog.setType(errorDialog.getScreenType());
                }
                errorDialog.asDialog(new DialogProperties(false));
                DialogHeaderPart header = new DialogHeaderPart();
                header.setHeaderIcon(IconType.Error);
                header.setHeaderText("Incompatible Versions");
                errorDialog.addMessagePart(MessagePartConstants.DialogHeader, header);
                errorDialog.setMessage(Arrays.asList(incompatibleVersionMessage.split("\n")));
                messageService.sendMessage(deviceId, errorDialog);
                return;
            }

            IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
            boolean created = false;
            DeviceModel sessionDevice = sessionAuthTracker.getDeviceModel(sessionId);
            String appId = sessionDevice != null ? sessionDevice.getAppId() : null;
            if (stateManager == null) {
                // If your first state has a
                stateManager = stateManagerContainer.create(appId, deviceId, queryParams, personalizationProperties);
                created = true;
            } else {
                stateManager.registerQueryParams(queryParams);
                stateManager.registerPersonalizationProperties(personalizationProperties);
                stateManager.sendConfigurationChangedMessage();
                stateManager.sendStartupCompleteMessage();
            }

            stateManagerContainer.setCurrentStateManager(stateManager);

            stateManager.setSessionAuthenticated(sessionId, sessionAuthTracker.isSessionAuthenticated(sessionId));
            stateManager.setSessionCompatible(sessionId, sessionAuthTracker.isSessionCompatible(sessionId));
            stateManager.setClientContext(clientContext);
            stateManager.getApplicationState().getScope().setDeviceScope("device", sessionAuthTracker.getDeviceModel(sessionId));

            eventPublisher.publish(new DeviceConnectedEvent(deviceId, appId, stateManager.getPairedDeviceId()));

            if (!created) {
                stateManager.refreshScreen();
            }

        } catch (Exception ex) {
            log.error("Failed to subscribe to " + topicName, ex);
            DialogUIMessage errorDialog = new DialogUIMessage();
            errorDialog.asDialog(new DialogProperties(false));
            DialogHeaderPart header = new DialogHeaderPart();
            header.setHeaderIcon(IconType.Error);
            header.setHeaderText("Failed To Subscribe");
            errorDialog.addMessagePart(MessagePartConstants.DialogHeader, header);
            errorDialog.setMessage(Arrays.asList(ex.getMessage()));
            messageService.sendMessage(deviceId, errorDialog);
        } finally {
            stateManagerContainer.setCurrentStateManager(null);
        }
    }

}
