package org.jumpmind.pos.service.strategy;

import org.jumpmind.pos.service.PosServerException;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(LocalOnlyStrategy.LOCAL_ONLY_STRATEGY)
public class LocalOnlyStrategy extends AbstractInvocationStrategy implements IInvocationStrategy {

    @Autowired
    protected ClientContext clientContext;

    static final String LOCAL_ONLY_STRATEGY = "LOCAL_ONLY";

    public String getStrategyName() {
        return LOCAL_ONLY_STRATEGY;
    }

    Map<Method, Method> defaultMethodMap  = new HashMap<>();
    Map<Method, Method> trainingMethodMap = new HashMap<>();

    @Override
    public Object invoke(List<String> profileIds, Object proxy, Method method, Map<String, Object> endpoints, Object[] args) throws Throwable {
        String path = buildPath(method);
        Object endpointObj = endpoints.get(path);
        Map<Method, Method> methodMap = getMethodMapForDeviceMode();
        if (endpointObj != null) {
            Method targetMethod = methodMap.get(method);
            if (targetMethod == null) {
                targetMethod = endpointObj.getClass().getMethod(method.getName(), method.getParameterTypes());
                methodMap.put(method, targetMethod);
            }
            if (targetMethod != null) {
                try {
                    return targetMethod.invoke(endpointObj, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        }

        throw new PosServerException(String.format("No endpoint found for path '%s' Please define a Spring-discoverable @Component class, "
                + "with a method annotated like  @Endpoint(path=\"%s\")", path, path));
    }

    private Map<Method, Method> getMethodMapForDeviceMode()  {
        return (getDeviceMode().equals("training") ? trainingMethodMap : defaultMethodMap);
    }

    private String getDeviceMode()  {
        String deviceMode = clientContext.get("deviceMode");
        return (deviceMode == null ? "default" : deviceMode);
    }
}
