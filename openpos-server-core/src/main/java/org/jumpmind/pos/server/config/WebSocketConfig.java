package org.jumpmind.pos.server.config;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jumpmind.pos.devices.DeviceNotAuthorizedException;
import org.jumpmind.pos.server.model.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import static org.jumpmind.pos.util.AppUtils.setupLogging;
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired(required = false)
    MutableBoolean initialized = new MutableBoolean(false);

    @Autowired
    Environment env;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /*
     * Default to 'v1' so pre-existing clients aren't broken
     */
    @Value("${openpos.general.compatibility.version:v1}")
    String serverCompatibilityVersion;

    @Value("${openpos.general.websocket.messageSizeLimit:8192000}")
    int messageSizeLimit;

    @Value("${openpos.general.websocket.sendBufferSizeLimit:8192000}")
    int sendBufferSizeLimit;

    @Value("${openpos.logging.messages.enabled:false}")
    boolean loggingEnabled;

    Map<String, SessionContext> deviceToSessionMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(messageSizeLimit); // 75681
        registration.setSendBufferSizeLimit(sendBufferSizeLimit);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic").setTaskScheduler(new DefaultManagedTaskScheduler()).setHeartbeatValue(new long[] { 0, 20000 });
        config.setApplicationDestinationPrefixes("/app");
        config.setPathMatcher(new AntPathMatcher("."));
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ExecutorChannelInterceptorAdapter() {

            @Override
            public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
                StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
                    String sessionId = (String) message.getHeaders().get("simpSessionId");
                    Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) message.getHeaders().get("nativeHeaders");
                    List<String> deviceToken = nativeHeaders.get("deviceToken");

                    if (!deviceToSessionMap.containsKey(deviceToken.get(0))) {
                        deviceToSessionMap.put(deviceToken.get(0), SessionContext.builder()
                                .sessionId(sessionId)
                                .build());
                    }

                    boolean isDeviceConnected = false;
                    Set<Map.Entry<String, SessionContext>> set = deviceToSessionMap.entrySet();
                    synchronized (deviceToSessionMap) {
                        Iterator<Map.Entry<String, SessionContext>> itr = set.iterator();
                        while (itr.hasNext()) {
                            Map.Entry<String, SessionContext> pair = itr.next();
                            if (pair.getValue().getSessionId().equals(sessionId)) {
                                isDeviceConnected = true;
                            }
                        }
                    }

                    if (!isDeviceConnected) {
                        log.warn("\n Client Connection Not Authorized While Session Exists: \n", new DeviceNotAuthorizedException());
                        return null;
                    }

                } else if (StompCommand.DISCONNECT.equals(headerAccessor.getCommand())) {
                    String sessionId = (String) message.getHeaders().get("simpSessionId");
                    Set<Map.Entry<String, SessionContext>> set = deviceToSessionMap.entrySet();
                    synchronized (deviceToSessionMap) {
                        Iterator<Map.Entry<String, SessionContext>> itr = set.iterator();
                        while (itr.hasNext()) {
                            Map.Entry<String, SessionContext> pair = itr.next();
                            if (pair.getValue().getSessionId().equals(sessionId)) {
                                itr.remove();
                            }
                        }
                    }
                }
                return super.beforeHandle(message, channel, handler);
            }

            @Override
            public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler, Exception ex) {
                SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
                if (accessor.getMessageType() == SimpMessageType.SUBSCRIBE && handler instanceof AbstractBrokerMessageHandler) {
                    /*
                     * https://stackoverflow.com/questions/29194530/stomp-over-
                     * websocket-using-spring-and-sockjs-message-lost
                     * 
                     * Publish a new session subscribed event AFTER the client
                     * has been subscribed to the broker. Before spring was
                     * publishing the event after receiving the message but not
                     * necessarily after the subscription occurred. There was a
                     * race condition because the subscription was being done on
                     * a separate thread.
                     */
                    applicationEventPublisher.publishEvent(new SessionSubscribedEvent(this, message));
                }
            }
        });

    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        /*
         * https://stackoverflow.com/questions/29689838/sockjs-receive-stomp-
         * messages-from-spring-websocket-out-of-order
         */
        registration.taskExecutor().maxPoolSize(1).corePoolSize(1);
        registration.interceptors(new ExecutorChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                Message<?> returnMessage = message;
                SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
                if (accessor != null) {
                    SimpMessageType messageType = accessor.getMessageType();
                    if (messageType == SimpMessageType.MESSAGE) {
                        // Add compatibility version to message headers going
                        // out to client
                        if (WebSocketConfig.this.serverCompatibilityVersion != null) {
                            accessor.addNativeHeader(MessageUtils.COMPATIBILITY_VERSION_HEADER,
                                    WebSocketConfig.this.serverCompatibilityVersion);
                            returnMessage = MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                        }
                    }
                }
                return returnMessage;
            }

            @Override
            public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler, Exception ex) {
                SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
                SimpMessageType messageType = accessor.getMessageType();
                if (messageType == SimpMessageType.MESSAGE && loggingEnabled) {
                    String[] tokens = accessor.getDestination().split("/");
                    setupLogging(tokens[2]);
                    log.info("Post send of message for session " + accessor.getSessionId() + " with destination " + accessor.getDestination() + ":\n" + new String((byte[]) message.getPayload()));
                }
            }
        });
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api").setAllowedOrigins("*").withSockJS().setInterceptors(new HandshakeInterceptor() {

            @Override
            public void afterHandshake(
                    ServerHttpRequest request,
                    ServerHttpResponse response,
                    WebSocketHandler wsHandler,
                    Exception exception) {
            }

            @Override
            public boolean beforeHandshake(
                    ServerHttpRequest request,
                    ServerHttpResponse response,
                    WebSocketHandler wsHandler,
                    Map<String, Object> attributes) throws Exception {
                if (!initialized.booleanValue()) {
                    log.info("Rejected websocket communication attempt because the server is not initialized");
                    return false;
                } else {
                    return true;
                }

            }
        });
    }

}