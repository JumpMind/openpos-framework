package org.jumpmind.pos.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.jumpmind.pos.util.clientcontext.ClientContextProperty;
import org.jumpmind.pos.util.clientcontext.ClientContextPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class EndpointInjector {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AutowireCapableBeanFactory applicationContext;

    @Autowired
    private ClientContext clientContext;
    
    @Autowired(required=false)
    List<IServiceContextProvider> serviceContextProviders; 

    public void performInjections(Object target, InjectionContext injectionConext) {
        // TODO CGlib wrapped objects have dropped our annotations....
        performInjectionsImpl(target, injectionConext);
        performPostContruct(target);
    }

    protected void performInjectionsImpl(Object target, InjectionContext context) {
        Class<?> targetClass = target.getClass();
        if (applicationContext != null) {
            applicationContext.autowireBean(target);
        }

        if( AopUtils.isAopProxy(target)){
            targetClass = AopUtils.getTargetClass(target);
            target = AopProxyUtils.getSingletonTarget(target);
        }

        while (targetClass != null) {
            performInjectionsImpl(targetClass, target, context);
            targetClass = targetClass.getSuperclass();
            if (targetClass == Object.class) {
                targetClass = null;
            }
        }
    }

    protected void performInjectionsImpl(Class<?> targetClass, Object target, InjectionContext context) {
        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            In in = field.getAnnotation(In.class);
            if (in == null) {
                in = field.getDeclaredAnnotation(In.class);
            }

            if (in != null) {
                field.setAccessible(true);
                injectField(targetClass, target, in.name(), in.required(), field, context);
            }

            ClientContextProperty clientContextProperty = field.getAnnotation(ClientContextProperty.class);
            if (clientContextProperty == null) {
                clientContextProperty = field.getDeclaredAnnotation(ClientContextProperty.class);
            }

            if( clientContextProperty != null ) {
                field.setAccessible(true);

                String clientContextPropertyName = clientContextProperty.name();
                if(StringUtils.isEmpty(clientContextPropertyName)){
                    clientContextPropertyName = field.getName();
                }

                try {
                    field.set(target, clientContext.get(clientContextPropertyName));
                } catch (Exception e ){
                    throw new PosServerException("Failed to apply injection. target=" + target, e);
                }

            }
        }
    }

    protected void injectField(Class<?> targetClass, Object target, String name, boolean required, Field field, InjectionContext context) {
        if (StringUtils.isEmpty(name)) {
            name = field.getName();
        }
        
        Object value = null;
        
        if (serviceContextProviders != null) {            
            for (IServiceContextProvider provider : serviceContextProviders) {
                value = provider.resolveValue(name, field.getType(), context);
                if (value != null) {
                    break;
                }
            }
        }

        if (value != null) {
            try {
                field.set(target, value);
            } catch (Exception ex) {
                throw new PosServerException("Failed to apply injection. target=" + target + " value=" + value, ex);
            }
        } else if (required) {
            throw failedToResolveInjection(field, name, targetClass, target, context);
        }
    }

    protected void performPostContruct(Object target) {
        Method[] methods = target.getClass().getDeclaredMethods();
        for (Method method : methods) {
            PostConstruct postConstructAnnotation = method.getAnnotation(PostConstruct.class);
            if (postConstructAnnotation != null) {
                method.setAccessible(true);
                try {
                    method.invoke(target);
                } catch (Exception ex) {
                    throw new PosServerException("Failed to invoke @PostConstruct method " + method, ex);
                }
            }
        }
    }

    private PosServerException failedToResolveInjection(Field field, String name, Class<?> targetClass, Object target, 
            InjectionContext context) {

        StringBuilder buff = new StringBuilder();
        buff.append(String.format("Failed to resolve required injection '%s' for field %s\n", name, field));
        buff.append("Tried the following contexts:\n");
        buff.append(printScopeValues(context));

        throw new PosServerException(buff.toString());
    }

    public String printScopeValues(InjectionContext context) {

        StringBuilder buff = new StringBuilder();

        buff.append("CONTEXT=").append(serviceContextProviders);

        return buff.toString();
    }
}
