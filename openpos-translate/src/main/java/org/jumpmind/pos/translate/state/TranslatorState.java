package org.jumpmind.pos.translate.state;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.StreamSupport;

import org.jumpmind.pos.core.device.DefaultDeviceResponse;
import org.jumpmind.pos.core.device.IDeviceRequest;
import org.jumpmind.pos.core.device.IDeviceResponse;
import org.jumpmind.pos.core.flow.*;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.service.IDeviceService;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.translate.ITranslationManager;
import org.jumpmind.pos.translate.ITranslationManagerSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

public class TranslatorState implements IState {

    final protected Logger logger = LoggerFactory.getLogger(getClass());

    @In(scope=ScopeType.Device)
    protected IStateManager stateManager;

    @In(scope=ScopeType.Device, required=false)
    @Out(scope=ScopeType.Device)
    protected ITranslationManager translationManager;

    @In(scope=ScopeType.Device, required=false)
    @Out(scope=ScopeType.Device)
    protected ITranslationManagerSubscriber subscriber;

    @Autowired
    protected IDeviceService deviceService;

    @Autowired
    protected Environment env;   
    
    @Autowired
    ApplicationContext applicationContext;    

    @Override
    public void arrive(Action action) {
        if (subscribe(action)) {
            translationManager.setStateManager(stateManager);
            translationManager.showActiveScreen();
        } else {
            translationManager.setStateManager(stateManager);
            translationManager.doAction(subscriber.getAppId(), action, new Form());
        }
    }
    
    protected ITranslationManager getTranslationManager() {
        return this.translationManager;
    }

    protected boolean subscribe(Action action) {
        if (subscriber == null || (action != null && "restart".equalsIgnoreCase(action.getName()))) {
            
            this.translationManager = applicationContext.getBean(ITranslationManager.class);  
            if (this.translationManager == null) {
                throw new IllegalStateException("When using a translation state, we expect an implementation of "
                        + ITranslationManager.class.getSimpleName() + " to be bound at the prototype scope");
            }
            
            logger.info("Creating new translation manager subscriber");
            
            this.subscriber = new ITranslationManagerSubscriber() {
                private static final long serialVersionUID = 1L;
                Properties properties;

                @Override
                public void showScreen(UIMessage screen) {
                    stateManager.showScreen(screen);
                }

                @Override
                public boolean isInTranslateState() {
                    return stateManager.getCurrentState() instanceof TranslatorState;
                }

                @Override
                public String getNodeId() {
                    return stateManager.getDeviceId();
                }

                @Override
                public String getAppId() {
                    return stateManager.getAppId();
                }

                @Override
                public Properties getProperties() {
                    if (properties == null) {
                        properties = new Properties();
                        MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
                        StreamSupport.stream(propSrcs.spliterator(), false).filter(ps -> ps instanceof EnumerablePropertySource)
                                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames()).flatMap(Arrays::<String> stream)
                                .forEach(propName -> properties.setProperty(propName, env.getProperty(propName)));
                    }
                    return properties;
                }

                @Override
                public void doAction(Action action) {
                    stateManager.doAction(action);
                }

                @Override
                public void addClientConfigurationTag(String tag){
                    List<String> tags = stateManager.getApplicationState().getScopeValue("additionalTagsForConfiguration");
                    if( tags == null ){
                        tags = new ArrayList<>();
                        stateManager.getApplicationState().getScope().setDeviceScope("additionalTagsForConfiguration", tags);
                    }
                    if( tags.stream().filter(t -> t.equals(tag)).count() < 1){
                        tags.add(tag);
                    }
                    stateManager.sendConfigurationChangedMessage();
                }

                @Override
                public void removeClientConfigurationTag(String tag){
                    List<String> tags = stateManager.getApplicationState().getScopeValue("additionalTagsForConfiguration");
                    if( tags != null) {
                        for (int i = 0; i < tags.size(); i++) {
                            if( tags.get(i).equals(tag)){
                                tags.remove(i);
                                break;
                            }
                        }

                    }
                    stateManager.sendConfigurationChangedMessage();
                }

                @Override
                public IDeviceResponse sendDeviceRequest(IDeviceRequest request) {
                    IDeviceResponse response = null;
                    CompletableFuture<IDeviceResponse> futureResponse = deviceService.send(stateManager.getAppId(), stateManager.getDeviceId(),
                            request);
                    try {
                        response = futureResponse.get(request.getTimeout(), TimeUnit.MILLISECONDS);
                        if (response != null) {
                            logger.info("Response of type '{}' received from device '{}', for request id: {}", response.getType(), response.getDeviceId(), response.getRequestId() );
                        } else {
                            logger.warn("Received a null response for request id: {}", request.getRequestId());
                        }
                    } catch (TimeoutException ex) {
                        logger.warn("Timeout ({}ms) reached for DeviceRequest id: {}, type: {}, subtype: {}.", request.getTimeout(), 
                                request.getRequestId(), request.getType(), request.getSubType() );
                        futureResponse.cancel(true);
                        response = new DefaultDeviceResponse(request.getRequestId(), request.getDeviceId(),
                                IDeviceResponse.DEVICE_TIMEOUT_RESPONSE_TYPE, "Timeout reached. " + ex.getMessage());
                    } catch (Exception ex) {
                        futureResponse.cancel(true);
                        String msg = String.format("Failure waiting for a response for DeviceRequest id: %s. Error: %s", request.getRequestId(), ex.getMessage());
                        response = new DefaultDeviceResponse(request.getRequestId(), request.getDeviceId(),
                                IDeviceResponse.DEVICE_ERROR_RESPONSE_TYPE, msg);
                        logger.error(msg);
                    }
                    return response;
                }
            };
            translationManager.setTranslationManagerSubscriber(subscriber);
            return true;
        } else {
            return false;
        }
    }

    
    @ActionHandler 
    public void onRestart(Action action) {
        this.arrive(action);
    }
    
    @ActionHandler
    public void onAnyAction(Action action, Form form) {
        translationManager.doAction(stateManager.getAppId(), action, form);
    }

}
