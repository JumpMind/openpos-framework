package org.jumpmind.pos.service.strategy;

import org.jumpmind.pos.service.EndpointInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

abstract public class AbstractInvocationStrategy {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected EndpointInjector endpointInjector;
    
    public static String buildPath(Method method) {
        StringBuilder path = new StringBuilder();
        RequestMapping clazzMapping = getRequestMapping(method);
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
    
    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotation) {
        Class<?> methodClazz = method.getDeclaringClass();
        A a = methodClazz.getAnnotation(annotation);
        if (a == null) {
            Class<?>[] interfaces = methodClazz.getInterfaces();
            for (Class<?> i : interfaces) {
                a = i.getAnnotation(annotation);
                if (a != null) {
                    break;
                }
            }
        }
        return a;
    }
    
    public static RequestMapping getRequestMapping(Method method) {
        return getAnnotation(method, RequestMapping.class);
    }
    
    public static RestController getRestController(Method method) {
        return getAnnotation(method, RestController.class);
    }
    
    public static String getServiceName(Method method) {
        RestController restController = getRestController(method);
        if (restController != null) {
            return restController.value();
        } else {
            return "";
        }
    }
}
