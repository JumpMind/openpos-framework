package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.core.service.spring.DeviceScope;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StateManagerBeanPostProcessor implements BeanPostProcessor {
    
    @Autowired
    private StateManagerContainer stateManagerContainer;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        IStateManager stateManager = stateManagerContainer.getCurrentStateManager();
        if (stateManager != null && stateManager.getInjector().hasInjections(bean)) {
                
            if (DeviceScope.isDeviceScope(beanName)) {                
                stateManager.performInjectionsOnSpringBean(bean);
            } else {
                throw new FlowException("Spring bean requests injections via @In but is not a"
                        + " device scoped bean. This should be changed to a device scoped bean. beanName=" + beanName + " bean=" + bean);
            }
        }
        
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {        
        return bean;
    }
}
