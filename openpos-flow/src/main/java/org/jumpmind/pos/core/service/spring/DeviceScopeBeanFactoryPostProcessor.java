package org.jumpmind.pos.core.service.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class DeviceScopeBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    
    @Override 
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        factory.registerScope("device", new DeviceScope(factory));
    }
}   