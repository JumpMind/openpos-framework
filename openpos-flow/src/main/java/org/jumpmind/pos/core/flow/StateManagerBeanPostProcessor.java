package org.jumpmind.pos.core.flow;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class StateManagerBeanPostProcessor implements BeanPostProcessor {
    
    @Autowired
    private StateManagerContainer stateManagerContainer;
    
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        IStateManager stateManager = stateManagerContainer.getCurrentStateManager();
        if (stateManager != null && stateManager.getInjector().hasInjections(bean)) {
            stateManager.performInjectionsOnSpringBean(bean);
        }
        
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {        
        return bean;
    }
}
