package org.jumpmind.pos.core.service;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.core.flow.IStateManagerContainer;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.service.IDevicesService;
import org.jumpmind.pos.devices.service.model.DisconnectDeviceRequest;
import org.jumpmind.pos.server.service.SessionConnectListener;
import org.jumpmind.pos.util.event.DeviceDisconnectedEvent;
import org.jumpmind.pos.util.event.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class SessionDisconnectedListener implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    IStateManagerContainer stateManagerContainer;

    @Autowired
    SessionConnectListener sessionAuthTracker;

    @Autowired
    EventPublisher eventPublisher;

    @Autowired
    IDevicesService devicesService;
    
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {        
        Message<?> msg = event.getMessage();
        String sessionId = (String) msg.getHeaders().get("simpSessionId");
        log.info("session disconnected: {}", sessionId);
        
        DeviceModel deviceModel = sessionAuthTracker.getDeviceModel(sessionId);

        if (deviceModel != null) {
            devicesService.disconnectDevice(new DisconnectDeviceRequest(deviceModel.getDeviceId(), deviceModel.getAppId()));

            try {
                eventPublisher.publish(new DeviceDisconnectedEvent(deviceModel.getDeviceId(), deviceModel.getAppId(), deviceModel.getPairedDeviceId()));
            } catch (Exception ex) {
                log.warn("Error publishing DeviceDisconnectedEvent", ex);
            }
        } else {
            log.warn("No device found for session id=" + sessionId + ", not publishing DeviceDisconnectedEvent.");
        }

        stateManagerContainer.removeSessionIdVariables(sessionId);
        stateManagerContainer.setCurrentStateManager(null);
    }

}