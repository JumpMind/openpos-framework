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

import static org.jumpmind.pos.service.strategy.AbstractInvocationStrategy.buildPath;

@Slf4j
@Component
public class EndpointInvoker implements InvocationHandler {

    @Autowired
    Map<String, IInvocationStrategy> strategies;

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private ClientContext clientContext;

    final static int MAX_SUMMARY_WIDTH = 127;

    @Autowired
    @Qualifier("ctxSession")
    @Lazy
    private DBSession dbSession;

    @Value("${openpos.installationId:'not set'}")
    String installationId;

    @Autowired
    Environment env;

    private final static Pattern serviceNamePattern = Pattern.compile("^(?<service>[^_]+)(_(?<version>\\d(_\\d)*))?$");
    private final static String implementationConfigPath = "openpos.services.specificConfig.%s.implementation";
    Map<String, Object> endPointsByPath;
    Map<String, Object> trainingEndPointsByPath;

    static BasicThreadFactory factory = new BasicThreadFactory.Builder().namingPattern("service-instrumentation-thread-%d").daemon(true)
            .build();
    private static final ExecutorService instrumentationExecutor = Executors.newSingleThreadExecutor(factory);
    protected HashMap<String, Boolean> endpointEnabledCache = new HashMap<String, Boolean>();

    public EndpointInvoker() {
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (endPointsByPath == null) {
            endPointsByPath = new HashMap<>();
            trainingEndPointsByPath = new HashMap<>();
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

                if ((implementation != null) && !implementation.equals(Endpoint.IMPLEMENTATION_DEFAULT)) {
                    log.info("Loading endpoints for the '{}' implementation of {} ({})", implementation, i.getSimpleName(),
                            serviceName);
                } else {
                    log.debug("Loading endpoints for the '{}' implementation of {} ({})", implementation, i.getSimpleName(),
                            serviceName);
                }

                //  For each endpoint, see if there is an override or special Training Mode version.
                //  Build out lists for both regular operations and Training Mode.

                Method[] methods = i.getMethods();
                for (Method method : methods) {
                    String path = buildPath(method);

                    //  See if there is an endpoint override bean for this service and path, both normal and Training Mode.

                    Object regularEndpointOverrideBean  = findBestEndpointOverrideMatch(path, implementation, endpointOverrides);
                    Object trainingEndpointOverrideBean = findBestEndpointOverrideMatch(path, "training", endpointOverrides);

                    //  Now see if there is a standard endpoint bean for this service and path. Again, both normal and
                    //  Training Mode.

                    Object regularEndpointBean  = findMatch(path, endpoints, implementation);
                    String regularEndpointImplementaton = implementation;
                    if (regularEndpointBean == null) {
                        //  Nothing for the current implementation, so try the default.
                        regularEndpointBean = findMatch(path, endpoints, Endpoint.IMPLEMENTATION_DEFAULT);
                        regularEndpointImplementaton = Endpoint.IMPLEMENTATION_DEFAULT;
                    }
                    if (regularEndpointBean == null) {
                        log.warn("No endpoint match found for service {}, path '{}', implementation '{}'", i.getSimpleName(), path, implementation);
                    }

                    Object trainingEndpointBean = findMatch(path, endpoints, "training");
                    if ((trainingEndpointBean != null) && (regularEndpointBean == null))  {
                        log.warn("Endpoint match found for service {}, path '{}', implementation 'training', but not implementation '{}' or default", i.getSimpleName(), path, implementation);
                    }

                    //  Given the endpoint beans we discovered above, decide which one will be
                    //  used in both normal scenarios and Training Mode.

                    if (trainingEndpointOverrideBean != null) {
                        log.info("Training override endpoint bean for service {}, path {}, implementation {}", i.getSimpleName(), path, "training");
                        trainingEndPointsByPath.put(path, trainingEndpointOverrideBean);
                        if (regularEndpointOverrideBean != null) {
                            log.info("Regular override endpoint bean for service {}, path {}, implementation {}", i.getSimpleName(), path, implementation);
                            endPointsByPath.put(path, regularEndpointOverrideBean);
                        }

                    }  else if (regularEndpointOverrideBean != null) {
                        log.debug("Regular override endpoint bean for service {}, path {}, implementation {}", i.getSimpleName(), path, implementation);
                        endPointsByPath.put(path, regularEndpointOverrideBean);
                        trainingEndPointsByPath.put(path, regularEndpointOverrideBean);

                    }  else if (trainingEndpointBean != null) {
                        log.info("Training endpoint bean for service {}, path {}, implementation {}", i.getSimpleName(), path, "training");
                        trainingEndPointsByPath.put(path, trainingEndpointBean);
                        if (regularEndpointBean != null)  {
                            log.info("Regular endpoint bean for service {}, path {}, implementation {}", i.getSimpleName(), path, regularEndpointImplementaton);
                            endPointsByPath.put(path, regularEndpointBean);
                       }

                    }  else if (regularEndpointBean != null)  {
                        log.debug("Regular endpoint bean for service {}, path {}, implementation {}", i.getSimpleName(), path, regularEndpointImplementaton);
                        endPointsByPath.put(path, regularEndpointBean);
                        trainingEndPointsByPath.put(path, regularEndpointBean);

                    }  else {
                        log.warn(String.format(
                            "No endpoint defined for path '%s' in the '%s' service. Please define a Spring-discoverable @Endpoint class, " +
                            "with a method annotated like  @Endpoint(\"%s\")", path, i.getSimpleName(), path));
                    }
                }
            }
        }
    }

    protected Map<String, Object> getEndpointsByPathMapForImplementation(String implementation)  {
        return ((implementation != null) && implementation.equals(Endpoint.IMPLEMENTATION_TRAINING) ? trainingEndPointsByPath : endPointsByPath);
    }

    protected Object findBestEndpointOverrideMatch(String path, String implementation, Collection<Object> endpointOverrides) {
        Object bestMatch = null;
        List<SimpleEntry<Object,EndpointOverride>> pathMatchedOverrides = endpointOverrides.stream()
                .map(o -> new SimpleEntry<>(o, ClassUtils.resolveAnnotation(EndpointOverride.class, o)))
                .filter(entry -> entry.getValue().path().equals(path)
                ).collect(Collectors.toList());

        if (pathMatchedOverrides.size() > 0) {
            List<SimpleEntry<Object, EndpointOverride>> implMatchedOverrides;
            if(pathMatchedOverrides.stream().anyMatch(entry -> entry.getValue().implementation().equals(implementation))) {
                implMatchedOverrides = pathMatchedOverrides.stream()
                        .filter(entry -> entry.getValue().implementation().equals(implementation))
                        .collect(Collectors.toList());
            }
            else {
                implMatchedOverrides = pathMatchedOverrides.stream()
                        .filter(entry -> entry.getValue().implementation().equals(Endpoint.IMPLEMENTATION_DEFAULT))
                        .collect(Collectors.toList());
            }

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

        if(StringUtils.isBlank(implementation)) {
            Matcher serviceNameMatcher = serviceNamePattern.matcher(serviceName);
            serviceNameMatcher.matches();
            String versionLessServiceName = serviceNameMatcher.group("service");
            implementation = env
                    .getProperty(String.format(implementationConfigPath, versionLessServiceName), Endpoint.IMPLEMENTATION_DEFAULT);
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
        if ((endPointsByPath == null) || (trainingEndPointsByPath == null)) {
            throw new PosServerException("endPointsByPath == null and/or trainingEndPointsByPath == null.  This class has not been fully initialized by Spring");
        }

        String deviceMode = clientContext.get("deviceMode");
        String implementation = ((deviceMode != null) && deviceMode.equals("training") ? Endpoint.IMPLEMENTATION_TRAINING : Endpoint.IMPLEMENTATION_DEFAULT);
        Map<String, Object> endpointsByPathMap = getEndpointsByPathMapForImplementation(implementation);
        Object endpointObj = endpointsByPathMap.get(path);

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
            profileIds.addAll(config.getProfileIds() != null ? config.getProfileIds(): Arrays.asList());
        }
        if (profileIds.size() == 0) {
            profileIds.add("local");
        }

        EndpointInvocationContext endpointInvocationContext = EndpointInvocationContext.builder()
                .profileIds(profileIds)
                .strategy(strategy)
                .config(config)
                .proxy(proxy)
                .method(method)
                .endpointImplementation(endpointImplementation)
                .endpointsByPathMap(endpointsByPathMap)
                .clientVersion(clientContext.get("version.nu-commerce"))
                .endpointPath(path)
                .endpoint(endpointObj)
                .arguments(args).build();

        return invokeStrategy(endpointInvocationContext);
    }

    protected Object invokeStrategy(EndpointInvocationContext endpointInvocationContext) throws Throwable {
        ServiceSampleModel sample = startSample(endpointInvocationContext);
        Object result = null;
        try {
            log(endpointInvocationContext);

            filterRequest(endpointInvocationContext);

            result = endpointInvocationContext.getStrategy().invoke(endpointInvocationContext);

            result = filerResult(endpointInvocationContext, result);
            endpointInvocationContext.setResult(result);

            endSampleSuccess(sample, endpointInvocationContext);
        } catch (Throwable ex) {
            endSampleError(sample, endpointInvocationContext, ex);
            throw ex;
        }

        return result;
    }

    private void log(EndpointInvocationContext endpointInvocationContext) {
        Method method = endpointInvocationContext.getMethod();
        if (!method.isAnnotationPresent(SuppressMethodLogging.class)) {
            if (log.isInfoEnabled()) {
                String implementation = endpointInvocationContext.getEndpointImplementation();
                log.info("Call endpoint: {}.{}() {} {}",
                        method.getDeclaringClass().getSimpleName(),
                        method.getName(),
                        endpointInvocationContext.getConfig() != null ? endpointInvocationContext.getConfig().getStrategy() : "",
                        implementation == null || Endpoint.IMPLEMENTATION_DEFAULT.equals(implementation) ? "" : implementation + " implementation");
            }
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
            EndpointInvocationContext endpointInvocationContext) {
        Method method = endpointInvocationContext.getMethod();

        if (method.isAnnotationPresent(Sample.class)) {
                ServiceSampleModel serviceSampleModel = new ServiceSampleModel();
                serviceSampleModel.setSampleId(installationId + System.currentTimeMillis());
                serviceSampleModel.setInstallationId(installationId);
                serviceSampleModel.setHostname(AppUtils.getHostName());
                serviceSampleModel.setServiceName(method.getDeclaringClass().getSimpleName() + "." + method.getName());
                serviceSampleModel.setServiceType(endpointInvocationContext.getStrategy().getStrategyName());
                serviceSampleModel.setStartTime(new Date());
                return serviceSampleModel;
        }
        return null;
    }

    protected void endSampleSuccess(
            ServiceSampleModel sample,
            EndpointInvocationContext endpointInvocationContext) {
        if (endpointInvocationContext.getResult() != null && sample != null) {
            sample.setServiceResult(StringUtils.abbreviate(endpointInvocationContext.getResult().toString(), MAX_SUMMARY_WIDTH));
        }
        endSample(sample, endpointInvocationContext);
    }

    protected void endSampleError(
            ServiceSampleModel sample,
            EndpointInvocationContext endpointInvocationContext,
            Throwable ex) {
        if (sample != null) {
            sample.setServiceResult(null);
            sample.setErrorFlag(true);
            sample.setErrorSummary(StringUtils.abbreviate(ex.getMessage(), MAX_SUMMARY_WIDTH));
            endSample(sample, endpointInvocationContext);
            endSample(sample, endpointInvocationContext);
        }
    }

    protected void endSample(ServiceSampleModel sample, EndpointInvocationContext endpointInvocationContext) {
        if (sample != null) {
            sample.setEndTime(new Date());
            sample.setDurationMs(sample.getEndTime().getTime() - sample.getStartTime().getTime());
            instrumentationExecutor.execute(() -> dbSession.save(sample));
        }
    }

    protected void filterRequest(EndpointInvocationContext endpointInvocationContext) {
        if (endpointInvocationContext.getEndpointPath().startsWith("/customer/")) {
            System.out.println("BEFORE. " + endpointInvocationContext.getEndpointPath());
        }
    }

    protected Object filerResult(EndpointInvocationContext endpointInvocationContext, Object result) {
        if (endpointInvocationContext.getClientVersion() != null
                && endpointInvocationContext.getClientVersion().startsWith("@version@")
                && endpointInvocationContext.getEndpointPath().equals("/customer/search")) {
            System.out.println("AFTER");
            return result;
        } else {
            return result;
        }
    }

}
