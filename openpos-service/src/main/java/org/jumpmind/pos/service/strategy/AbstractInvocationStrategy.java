package org.jumpmind.pos.service.strategy;

import java.lang.reflect.Method;

import org.jumpmind.pos.service.EndpointInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;

abstract public class AbstractInvocationStrategy {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected EndpointInjector endpointInjector;
    
    protected String buildPath(Method method) {
        StringBuilder path = new StringBuilder();
        Class<?> methodClazz = method.getDeclaringClass();
        RequestMapping clazzMapping = methodClazz.getAnnotation(RequestMapping.class);
        if (clazzMapping != null) {
            path.append(clazzMapping.value()[0]);
        }
        RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
        if (methodMapping != null) {
            if (methodMapping.path() != null && methodMapping.path().length > 0) {
                path.append(methodMapping.path()[0]);
            } else if (methodMapping.value() != null && methodMapping.value().length > 0) {
                path.append(methodMapping.value()[0]);
            }
        }
        return path.toString();
    }
}
