package org.jumpmind.pos.service.strategy;

import org.jumpmind.pos.service.PosServerException;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Component(LocalOnlyStrategy.LOCAL_ONLY_STRATEGY)
public class LocalOnlyStrategy extends AbstractInvocationStrategy implements IInvocationStrategy {

    static final String LOCAL_ONLY_STRATEGY = "LOCAL_ONLY";

    public String getStrategyName() {
        return LOCAL_ONLY_STRATEGY;
    }

    @Override
    public Object invoke(List<String> profileIds, Object proxy, Method method, Map<String, Object> endpoints, Object[] args) throws Throwable {
        String path = buildPath(method);
        Object obj = endpoints.get(path);
        if (obj != null) {
            Method targetMethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
            if (targetMethod != null) {
                try {
                    return targetMethod.invoke(obj, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        }

        throw new PosServerException(String.format("No endpoint found for path '%s' Please define a Spring-discoverable @Component class, "
                + "with a method annotated like  @Endpoint(path=\"%s\")", path, path));
    }

}
