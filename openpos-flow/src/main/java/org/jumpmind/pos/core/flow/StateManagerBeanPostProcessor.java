package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.core.service.spring.DeviceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StateManagerBeanPostProcessor implements BeanPostProcessor {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private StateManagerContainer stateManagerContainer;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        IStateManager stateManager = stateManagerContainer.getCurrentStateManager();
        if (stateManager != null && stateManager.getInjector().hasInjections(bean)) {
                
            boolean isPrototype = false;
            if (applicationContext instanceof ConfigurableApplicationContext) {
                BeanDefinition beanDef = ((ConfigurableApplicationContext) applicationContext).getBeanFactory().getBeanDefinition(beanName);
                isPrototype = beanDef != null && BeanDefinition.SCOPE_PROTOTYPE.equalsIgnoreCase(beanDef.getScope());
            }

            boolean doInjections = false;
            if (isPrototype) {
                // Prototype beans can be injected into any bean
                doInjections = true;
            } else {
                Scope scope = stateManager.getApplicationState().getScope();
                ScopeType beanScopeType = scope.getScopeType(beanName);
                if (beanScopeType == null && DeviceScope.isDeviceScope(beanName)) {
                    beanScopeType = ScopeType.Device;
                }

                if (beanScopeType != null && beanScopeType.rank() <= ScopeType.Device.rank()) {
                    doInjections = true;
                } else if (beanScopeType == null) {
                    if (!(bean instanceof ITransitionStep)){
                        throw new FlowException("Spring bean requests injections via @In but has an unknown ScopeType. "
                                + "This should be changed to a Device or lower scoped bean. beanName=" + beanName + " bean=" + bean);
                    }
                } else if (!(bean instanceof ITransitionStep)){
                    throw new FlowException("Spring bean requests injections via @In but has a higher level scope. "
                            + "This should be changed to a Device or lower scoped bean. beanName=" + beanName + " bean=" + bean);
                }
            }
            
            if (doInjections) {
                stateManager.performInjectionsOnSpringBean(bean);
            }
            
        }
        
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {        
        return bean;
    }
}
