package org.jumpmind.pos.service.strategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.jumpmind.pos.service.EndpointOverride;
import org.jumpmind.pos.service.InjectionContext;
import org.jumpmind.pos.service.PosServerException;
import org.jumpmind.pos.service.ServiceSpecificConfig;
import org.springframework.stereotype.Component;

@Component("LOCAL_ONLY")
public class LocalOnlyStrategy extends AbstractInvocationStrategy implements IInvocationStrategy {

    @Override
    public Object invoke(ServiceSpecificConfig config, Object proxy, Method method, Object[] args) throws Throwable {
        String path = buildPath(method);
        Object obj = applicationContext.getBean(path);
        Collection<Object> beans = applicationContext.getBeansWithAnnotation(EndpointOverride.class).values();
        for (Object testObj : beans) {
            EndpointOverride override = testObj.getClass().getAnnotation(EndpointOverride.class);
            if (override.path().equals(path)) {
                obj = testObj;
            }
        }

        if (obj != null) {
            endpointInjector.performInjections(obj, new InjectionContext(args));
            Method targetMethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
            if (targetMethod != null) {
                try {
                    return targetMethod.invoke(obj, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        }

        throw new PosServerException(String.format("No endpoint found for path '%s' Please define a Spring-discoverable @Componant class, "
                + "with a method annotated like  @Endpoint(\"%s\")", path, path));
    }

}
