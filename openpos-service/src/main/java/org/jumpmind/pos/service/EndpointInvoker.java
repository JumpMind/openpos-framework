package org.jumpmind.pos.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.service.instrumentation.Sample;
import org.jumpmind.pos.service.instrumentation.ServiceSampleModel;
import org.jumpmind.pos.service.strategy.AbstractInvocationStrategy;
import org.jumpmind.pos.service.strategy.IInvocationStrategy;
import org.jumpmind.pos.util.AppUtils;
import org.jumpmind.pos.util.ClassUtils;
import org.jumpmind.pos.util.SuppressMethodLogging;
import org.jumpmind.pos.util.clientcontext.ClientContext;
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
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;
import static org.jumpmind.pos.service.strategy.AbstractInvocationStrategy.buildPath;

@Slf4j
@Component
public class EndpointInvoker implements InvocationHandler {

    @Autowired
    Map<String, IInvocationStrategy> strategies;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ServiceConfig serviceConfig;

    final static int MAX_SUMMARY_WIDTH = 127;

    @Autowired
    @Qualifier("ctxSession")
    @Lazy
    DBSession dbSession;

    @Value("${openpos.installationId:'not set'}")
    String installationId;

    @Autowired
    Environment env;

    @Autowired
    private ClientContext clientContext;

    private final static Pattern serviceNamePattern = Pattern.compile("^(?<service>[^_]+)(_(?<version>\\d(_\\d)*))?$");
    private final static String implementationConfigPath = "openpos.services.specificConfig.%s.implementation";

    Map<String, Object> endPointsByPath;

    Map<String, Endpoint> annotationsByPath;

    static BasicThreadFactory factory = new BasicThreadFactory.Builder().
            namingPattern("service-instrumentation-thread-%d").daemon(true).
            build();

    private static final ExecutorService instrumentationExecutor = Executors.newSingleThreadExecutor(factory);

    public EndpointInvoker() {
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (endPointsByPath == null) {
            endPointsByPath = new HashMap<>();
            annotationsByPath = new HashMap<>();
            Collection<Object> beans = applicationContext.getBeansWithAnnotation(RestController.class).values();
            if (beans != null) {
                for (Object object : beans) {
                    buildEndpointMappingsForService(object);
                }
            }
        }
    }

    public void buildEndpointMappingsForService(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        Collection<Object> endpointOverrides = applicationContext.getBeansWithAnnotation(EndpointOverride.class).values();
        Collection<Object> endpoints = applicationContext.getBeansWithAnnotation(Endpoint.class).values();
        //if we have any endpoints that are an @EndpointOverride and extend an Endpoint with @Endpoint they were included
        //in the collection of endpoints and should therefore be filtered out.
        endpoints = endpoints.stream().filter(e -> !endpointOverrides.contains(e)).collect(Collectors.toList());

        for (Class<?> i : interfaces) {
            RestController controller = i.getAnnotation(RestController.class);
            if (controller != null) {
                String serviceName = controller.value();
                String implementation = getServiceImplementation(serviceName);
                if (implementation != null && !implementation.equals("default")) {
                    log.info("Loading endpoints for the '{}' implementation of {}({})", implementation, i.getSimpleName(),
                            serviceName);
                } else {
                    log.debug("Loading endpoints for the '{}' implementation of {}({})", implementation, i.getSimpleName(),
                            serviceName);
                }
                Method[] methods = i.getMethods();
                for (Method method : methods) {
                    String path = buildPath(method);
                    Object endpointOverride = findBestEndpointOverrideMatch(path, implementation, endpointOverrides);
                    if (endpointOverride != null) {
                        endPointsByPath.put(path, endpointOverride);
                    }

                    if (!endPointsByPath.containsKey(path)) {
                        Object endpointBean = findMatch(path, endpoints, implementation);
                        if (endpointBean == null) {
                            endpointBean = findMatch(path, endpoints, "default");
                        }

                        if (endpointBean != null) {
                            endPointsByPath.put(path, endpointBean);
                            annotationsByPath.put(path, endpointBean.getClass().getAnnotation(Endpoint.class));
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

    protected Object findBestEndpointOverrideMatch(String path, String implementation, Collection<Object> endpointOverrides) {
        Object bestMatch = null;
        List<SimpleEntry<Object, EndpointOverride>> pathMatchedOverrides = endpointOverrides.stream()
                .map(o -> new SimpleEntry<Object, EndpointOverride>(o, ClassUtils.resolveAnnotation(EndpointOverride.class, o)))
                .filter(entry -> entry.getValue().path().equals(path)
                ).collect(Collectors.toList());

        if (pathMatchedOverrides.size() > 0) {
            List<SimpleEntry<Object, EndpointOverride>> implMatchedOverrides = pathMatchedOverrides.stream()
                    .filter(entry -> entry.getValue().implementation().equals(implementation)).collect(Collectors.toList());
            if (implMatchedOverrides.size() > 1) {
                throw new IllegalStateException(
                        String.format("Found %d EndpointOverrides having path '%s' and implementation '%s'. Expected only one.",
                                implMatchedOverrides.size(), path, implementation)
                );
            } else if (implMatchedOverrides.size() == 1) {
                log.info("Endpoint at path '{}' overridden with {}", path, implMatchedOverrides.get(0).getKey().getClass().getName());
                bestMatch = implMatchedOverrides.get(0).getKey();
            }
        }

        return bestMatch;
    }

    protected String getServiceImplementation(String serviceName) {
        String implementation = env.getProperty(String.format(implementationConfigPath, serviceName));

        if (StringUtils.isBlank(implementation)) {
            Matcher serviceNameMatcher = serviceNamePattern.matcher(serviceName);
            serviceNameMatcher.matches();
            String versionLessServiceName = serviceNameMatcher.group("service");
            implementation = env
                    .getProperty(String.format(implementationConfigPath, versionLessServiceName), "default");
        }

        return implementation;
    }

    protected Object findMatch(String path, Collection<Object> endpoints, String implementation) {
        for (Object endpointBean : endpoints) {
            Endpoint endPoint = ClassUtils.resolveAnnotation(Endpoint.class, endpointBean);
            if (endPoint == null)  {
                log.warn("No @Endpoint annotation found for endpoint class {}, path {}, implementation {}", endpointBean.getClass().getSimpleName(), path, implementation);
            } else if (endPoint.path().equals(path) && endPoint.implementation().equals(implementation)) {
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
        if (endPointsByPath == null) {
            throw new PosServerException("endPointsByPath == null.  This class has not been fully initialized by Spring");
        }
        Object endpointObj = endPointsByPath.get(path);
        ServiceSpecificConfig config = getSpecificConfig(method);
        EndpointSpecificConfig endConfig = null;
        String endpointImplementation = null;
        if(endpointObj != null) {
            EndpointOverride override = endpointObj.getClass().getAnnotation(EndpointOverride.class);
            if (override != null) {
                for (EndpointSpecificConfig endpointSpecificConfig : config.getEndpoints()) {
                    if (override.path().equals(endpointSpecificConfig.getPath())) {
                        endpointImplementation = override.implementation();
                        endConfig = endpointSpecificConfig;
                    }
                }
            }
            else {
                Endpoint annotation = endpointObj.getClass().getAnnotation(Endpoint.class);

                if (annotation != null) {
                    for (EndpointSpecificConfig endpointSpecificConfig : config.getEndpoints()) {
                        if (annotation.path().equals(endpointSpecificConfig.getPath())) {
                            endpointImplementation = annotation.implementation();
                            endConfig = endpointSpecificConfig;
                        }
                    }
                }
            }
        }

        IInvocationStrategy strategy;
        List<String> profileIds = new ArrayList<>();
        if (endConfig != null) {
            strategy = strategies.get(endConfig.getStrategy().name());
            profileIds.add(endConfig.getProfile());
        } else {
            strategy = strategies.get(config.getStrategy().name());
            profileIds.addAll(config.getProfileIds() != null ? config.getProfileIds() : Arrays.asList());
        }
        if (profileIds.size() == 0) {
            profileIds.add("local");
        }
        ServiceSampleModel sample = startSample(strategy, config, proxy, method, args);
        Object result = null;
        try {
            log(method, args, endpointImplementation);
            result = strategy.invoke(profileIds, proxy, method, endPointsByPath, args);
            endSampleSuccess(sample, config, proxy, method, args, result);
        } catch (Throwable ex) {
            endSampleError(sample, config, proxy, method, args, result, ex);
            throw ex;
        }

        return result;
    }

    private void log(Method method, Object[] args, String implementation) {
        if (!method.isAnnotationPresent(SuppressMethodLogging.class)) {
            StringBuilder logArgs = new StringBuilder();
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (arg instanceof CharSequence) {
                        logArgs.append("'");
                        logArgs.append(arg.toString());
                        logArgs.append("'");
                    } else if (arg != null) {
                        logArgs.append(arg.toString());
                    } else {
                        logArgs.append("null");
                    }
                    if (args.length - 1 > i) {
                        logArgs.append(",");
                    }
                }
            }
            log.info("{}.{}() {}",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    implementation == null || "default".equals(implementation) ? "" : implementation + " implementation");
        }
    }

    private ServiceSpecificConfig getSpecificConfig(Method method) {
        String serviceName = AbstractInvocationStrategy.getServiceName(method);
        if (StringUtils.isNotBlank(serviceName)) {
            String deviceId = clientContext.get("deviceId");
            if(deviceId == null) {
                deviceId = "no-device";
            }
            return serviceConfig.getServiceConfig(deviceId, serviceName);
        } else {
            throw new IllegalStateException(method.getDeclaringClass().getSimpleName() + " must declare @"
                    + RestController.class.getSimpleName() + " and it must have the value() attribute set");
        }
    }

    protected ServiceSampleModel startSample(
            IInvocationStrategy strategy,
            ServiceSpecificConfig config,
            Object proxy,
            Method method,
            Object[] args) {
        ServiceSampleModel sample = null;
        if (method.isAnnotationPresent(Sample.class)) {
            sample = new ServiceSampleModel();
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
            ServiceSampleModel sample,
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
            ServiceSampleModel sample,
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

    protected void endSample(ServiceSampleModel sample, ServiceSpecificConfig config, Object proxy, Method method, Object[] args) {
        if (sample != null) {
            sample.setEndTime(new Date());
            sample.setDurationMs(sample.getEndTime().getTime() - sample.getStartTime().getTime());
            instrumentationExecutor.execute(() -> dbSession.save(sample));
        }
    }

}
