package org.jumpmind.pos.service;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.jumpmind.pos.service.strategy.IInvocationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
public class EndpointDispatchInvocationHandler implements InvocationHandler {

    @Autowired
    Map<String, IInvocationStrategy> strategies;

    @Autowired
    private ServiceConfig serviceConfig;

    @Value("${openpos.installationId}")
    String installationId;

    public EndpointDispatchInvocationHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("equals")) {
            return false;
        }

        ServiceSpecificConfig config = getSpecificConfig(method);
        IInvocationStrategy strategy = strategies.get(config.getStrategy().name());
        return strategy.invoke(config, proxy, method, args);

    }
    
    private ServiceSpecificConfig getSpecificConfig(Method method) {
        Class<?> methodClazz = method.getDeclaringClass();
        RestController restController = methodClazz.getAnnotation(RestController.class);
        if (restController != null && isNotBlank(restController.value())) {
            return serviceConfig.getServiceConfig(installationId, restController.value());
        } else {
            throw new IllegalStateException(methodClazz.getSimpleName() + " must declare @" + RestController.class.getSimpleName()
                    + " and it must have the value() attribute set");
        }
    }
    




}
