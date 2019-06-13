package org.jumpmind.pos.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.service.instrumentation.Sample;
import org.jumpmind.pos.service.instrumentation.ServiceSample;
import org.jumpmind.pos.service.strategy.AbstractInvocationStrategy;
import org.jumpmind.pos.service.strategy.IInvocationStrategy;
import org.jumpmind.pos.util.AppUtils;
import org.jumpmind.pos.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.jumpmind.pos.service.strategy.AbstractInvocationStrategy.buildPath;

@Component
public class EndpointDispatchInvocationHandler implements InvocationHandler {

    @Autowired
    Map<String, IInvocationStrategy> strategies;

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    private ServiceConfig serviceConfig;

    final static int MAX_SUMMARY_WIDTH = 127;

    @Autowired
    @Qualifier("ctxSession")
    @Lazy
    private DBSession dbSession;

    @Value("${openpos.installationId}")
    String installationId;

    @Autowired
    Environment env;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    Map<String, Object> endPointsByPath;

    static BasicThreadFactory factory = new BasicThreadFactory.Builder().namingPattern("service-instrumentation-thread-%d").daemon(true)
            .build();
    private static final ExecutorService instrumentationExecutor = Executors.newSingleThreadExecutor(factory);

    public EndpointDispatchInvocationHandler() {
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (endPointsByPath == null) {
            endPointsByPath = new HashMap<>();
            Collection<Object> beans = applicationContext.getBeansWithAnnotation(RestController.class).values();
            if (beans != null) {
                Collection<Object> endpointOverrides = applicationContext.getBeansWithAnnotation(EndpointOverride.class).values();
                Collection<Object> endpoints = applicationContext.getBeansWithAnnotation(Endpoint.class).values();
                for (Object object : beans) {
                    Class<?>[] interfaces = object.getClass().getInterfaces();
                    for (Class<?> i : interfaces) {
                        RestController controller = i.getAnnotation(RestController.class);
                        if (controller != null) {
                            String serviceName = controller.value();
                            String implementation = env
                                    .getProperty(String.format("openpos.services.specificConfig.%s.implementation", serviceName), "default");
                            log.info("Loading endpoints for the '{}' implementation of {}({})", implementation, i.getSimpleName(),
                                    serviceName);
                            Method[] methods = i.getMethods();
                            for (Method method : methods) {
                                String path = buildPath(method);
                                for (Object overridenBean : endpointOverrides) {
                                    EndpointOverride override = ClassUtils.resolveAnnotation(EndpointOverride.class, overridenBean);
                                    if (override.path().equals(path) && override.implementation().equals(implementation)) {
                                        endPointsByPath.put(path, overridenBean);
                                        break;
                                    }
                                }

                                if (!endPointsByPath.containsKey(path)) {
                                    Object endpointBean = findMatch(path, endpoints, implementation);
                                    if (endpointBean == null) {
                                        endpointBean = findMatch(path, endpoints, "default");
                                    }

                                    if (endpointBean != null) {
                                        endPointsByPath.put(path, endpointBean);
                                    } else {
                                        log.warn(String.format(
                                                "No endpoint defined for path '%s' in '%s' in the service Please define a Spring-discoverable @Endpoint class, "
                                                        + "with a method annotated like  @Endpoint(\"%s\")",
                                                path, i.getSimpleName(), path));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected Object findMatch(String path, Collection<Object> endpoints, String implementation) {
        for (Object endpointBean : endpoints) {
            Endpoint endPoint = ClassUtils.resolveAnnotation(Endpoint.class, endpointBean);
            if (endPoint.path().equals(path) && endPoint.implementation().equals(implementation)) {
                return endpointBean;
            }
        }
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("equals")) {
            return false;
        }
        String path = buildPath(method);
        Object obj = endPointsByPath.get(path);
        ServiceSpecificConfig config = getSpecificConfig(method);
        EndpointSpecificConfig endConfig = null;
        if (obj != null) {
            for (EndpointSpecificConfig aWeirdAcronym : config.getEndpoints()) {
                if (obj.getClass().getSuperclass().getAnnotation(Endpoint.class).path().equals(aWeirdAcronym.getPath()))
                    endConfig = aWeirdAcronym;
            }
        }
        IInvocationStrategy strategy;
        if (endConfig != null) {
            strategy = strategies.get(endConfig.getStrategy().name());
        } else {
            strategy = strategies.get(config.getStrategy().name());
        }
        ServiceSample sample = startSample(strategy, config, proxy, method, args);
        Object result = null;
        try {
            result = strategy.invoke(config, proxy, method, endPointsByPath, args);
            endSampleSuccess(sample, config, proxy, method, args, result);
        } catch (Throwable ex) {
            endSampleError(sample, config, proxy, method, args, result, ex);
            throw ex;
        }

        return result;
    }

    private ServiceSpecificConfig getSpecificConfig(Method method) {
        String serviceName = AbstractInvocationStrategy.getServiceName(method);
        if (isNotBlank(serviceName)) {
            return serviceConfig.getServiceConfig(installationId, serviceName);
        } else {
            throw new IllegalStateException(method.getDeclaringClass().getSimpleName() + " must declare @"
                    + RestController.class.getSimpleName() + " and it must have the value() attribute set");
        }
    }

    protected ServiceSample startSample(
            IInvocationStrategy strategy,
            ServiceSpecificConfig config,
            Object proxy,
            Method method,
            Object[] args) {
        ServiceSample sample = null;
        if (method.isAnnotationPresent(Sample.class)) {
            sample = new ServiceSample();
            sample.setSampleId(installationId + System.currentTimeMillis());
            sample.setInstallationId(installationId);
            sample.setHostname(AppUtils.getHostName());
            sample.setServiceName(method.getDeclaringClass().getSimpleName() + "." + method.getName());
            sample.setServiceType(strategy.getStrategyName());
            sample.setStartTime(new Date());
        }
        return sample;
    }

    protected void endSampleSuccess(
            ServiceSample sample,
            ServiceSpecificConfig config,
            Object proxy,
            Method method,
            Object[] args,
            Object result) {
        if (result != null && sample != null) {
            sample.setServiceResult(StringUtils.abbreviate(result.toString(), MAX_SUMMARY_WIDTH));
        }
        endSample(sample, config, proxy, method, args);
    }

    protected void endSampleError(
            ServiceSample sample,
            ServiceSpecificConfig config,
            Object proxy,
            Method method,
            Object[] args,
            Object result,
            Throwable ex) {
        if (sample != null) {
            sample.setServiceResult(null);
            sample.setErrorFlag(true);
            sample.setErrorSummary(StringUtils.abbreviate(ex.getMessage(), MAX_SUMMARY_WIDTH));
            endSample(sample, config, proxy, method, args);
        }
    }

    protected void endSample(ServiceSample sample, ServiceSpecificConfig config, Object proxy, Method method, Object[] args) {
        if (sample != null) {
            sample.setEndTime(new Date());
            sample.setDurationMs(sample.getEndTime().getTime() - sample.getStartTime().getTime());
            instrumentationExecutor.execute(() -> dbSession.save(sample));
        }
    }

}
