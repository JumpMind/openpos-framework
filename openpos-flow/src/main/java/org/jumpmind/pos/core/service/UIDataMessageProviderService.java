package org.jumpmind.pos.core.service;

import org.jumpmind.pos.core.flow.ApplicationState;
import org.jumpmind.pos.core.flow.IMessageInterceptor;
import org.jumpmind.pos.core.ui.UIDataMessage;
import org.jumpmind.pos.core.ui.data.IHasObservableUIDataMessageProviderProperty;
import org.jumpmind.pos.core.ui.data.UIDataMessageProvider;
import org.jumpmind.pos.core.ui.data.UIDataMessageProviderPropertyChangeEvent;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

@Component
public class UIDataMessageProviderService implements PropertyChangeListener {

    @Autowired
    IMessageService messageService;

    @Autowired
    ApplicationContext applicationContext;

    public void updateProviders(ApplicationState applicationState, Map<String, UIDataMessageProvider> uiDataMessageProviders){
        if(uiDataMessageProviders != null) {
            uiDataMessageProviders.forEach((key, provider) -> {
                provider.setProviderKey(key);
            });
        }
        if( applicationState.getDataMessageProviderMap() != null ){
            //clean up old providers
            List<String> keysToRemove = new ArrayList<>();
            applicationState.getDataMessageProviderMap().keySet().forEach( key -> {
                if( uiDataMessageProviders == null || !uiDataMessageProviders.containsKey(key)){
                    //If the provider is not in the new map send a message to clean it up on the client
                    sendDataMessage(applicationState.getAppId(), applicationState.getDeviceId(), null, key, -1 );
                    keysToRemove.add(key);
                }
            });
            keysToRemove.forEach( key -> {
                UIDataMessageProvider provider = applicationState.getDataMessageProviderMap().get(key);
                if(provider instanceof IHasObservableUIDataMessageProviderProperty) {
                    ((IHasObservableUIDataMessageProviderProperty)provider).removePropertyChangeListener(this);
                }
                applicationState.getDataMessageProviderMap().remove(key);
            });

            if( uiDataMessageProviders != null ){
                uiDataMessageProviders.forEach( (key, provider) -> {
                    boolean inMap = applicationState.getDataMessageProviderMap().containsKey(key);
                    if(!inMap) {
                        if(provider instanceof IHasObservableUIDataMessageProviderProperty) {
                            ((IHasObservableUIDataMessageProviderProperty)provider).addPropertyChangeListener(this);
                        }
                        //If it is a new provider add it and initialize it
                        applicationState.getDataMessageProviderMap().put(key, provider);
                        sendDataMessage(applicationState.getAppId(), applicationState.getDeviceId(), provider.getNextDataChunk(), key, provider.getSeriesId() );
                    }
                    if(provider.isNewSeries()) {
                        provider.setSeriesId( provider.getSeriesId() + 1);
                        provider.setNewSeries(false);
                        //If it is a new series add it and initialize it
                        applicationState.getDataMessageProviderMap().put(key, provider);
                        sendDataMessage(applicationState.getAppId(), applicationState.getDeviceId(), provider.getNextDataChunk(), key, provider.getSeriesId() );
                    }
                });
            }

        } else if(uiDataMessageProviders != null) {
            //If they are all new initialize them all
            applicationState.setDataMessageProviderMap(uiDataMessageProviders);
            uiDataMessageProviders.forEach( (key, provider) -> {
                if(provider instanceof IHasObservableUIDataMessageProviderProperty) {
                    //remove this listener first in case it is coming through here on a screen refresh
                    // instead of initially, in which case, the listener was already added previously
                    ((IHasObservableUIDataMessageProviderProperty)provider).removePropertyChangeListener(this);
                    ((IHasObservableUIDataMessageProviderProperty)provider).addPropertyChangeListener(this);
                }
                sendDataMessage(applicationState.getAppId(), applicationState.getDeviceId(), provider.getNextDataChunk(), key, provider.getSeriesId() );
            });
        }
    }

    public boolean handleAction(Action action, ApplicationState applicationState){
        Map<String, UIDataMessageProvider> dataMessageProviderMap = applicationState.getDataMessageProviderMap();
        Optional<String> providerKey = null;
        if(dataMessageProviderMap != null){
            providerKey = dataMessageProviderMap.keySet().stream().filter(key -> action.getName().contains(key)).findFirst();
        }

        if( providerKey == null || !providerKey.isPresent()) {
            return false;
        }

        UIDataMessageProvider uiDataMessageProvider = dataMessageProviderMap.get(providerKey.get());
        sendDataMessage(applicationState.getAppId(), applicationState.getDeviceId(), uiDataMessageProvider.getNextDataChunk(), providerKey.get(), uiDataMessageProvider.getSeriesId() );

        return true;
    }

    public void resetProviders(ApplicationState applicationState) {
        if( applicationState.getDataMessageProviderMap() != null ){
            applicationState.getDataMessageProviderMap().forEach( (key, provider) -> {
                provider.reset();
            });
        }

        applicationState.setDataMessageProviderMap(null);
    }

    private void sendDataMessage(String appId, String deviceId, List<Object> data, String dataType, int series ) {

        String[] screenInterceptorBeanNames = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(IMessageInterceptor.class, UIDataMessage.class));
        UIDataMessage message = UIDataMessage.builder()
                .data(data)
                .dataType( dataType )
                .seriesId( series )
                .build();

        if (screenInterceptorBeanNames != null) {
            for (String beanName: screenInterceptorBeanNames) {
                @SuppressWarnings("unchecked")
                IMessageInterceptor<UIDataMessage> screenInterceptor =  (IMessageInterceptor<UIDataMessage>) applicationContext.getBean(beanName);
                screenInterceptor.intercept(appId, deviceId, message);

            }
        }

        messageService.sendMessage(appId, deviceId, message);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt instanceof UIDataMessageProviderPropertyChangeEvent) {
            UIDataMessageProvider uiDataMessageProvider = (UIDataMessageProvider) evt.getSource();
            sendDataMessage(((UIDataMessageProviderPropertyChangeEvent) evt).getAppId(),
                    ((UIDataMessageProviderPropertyChangeEvent) evt).getDeviceId(),
                    uiDataMessageProvider.getNextDataChunk(),
                    uiDataMessageProvider.getProviderKey(),
                    uiDataMessageProvider.getSeriesId());
        }
    }
}
