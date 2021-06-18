package org.jumpmind.pos.service.strategy;

import static org.jumpmind.pos.util.RestApiSupport.REST_API_TOKEN_HEADER_NAME;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jumpmind.pos.service.PosServerException;
import org.jumpmind.pos.service.ProfileConfig;
import org.jumpmind.pos.service.ServiceConfig;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.jumpmind.pos.util.model.ServiceException;
import org.jumpmind.pos.util.model.ServiceResult;
import org.jumpmind.pos.util.model.ServiceVisit;
import org.jumpmind.pos.util.status.IStatusManager;
import org.jumpmind.pos.util.status.IStatusReporter;
import org.jumpmind.pos.util.status.Status;
import org.jumpmind.pos.util.status.StatusReport;
import org.jumpmind.pos.util.web.ConfiguredRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

@Component(RemoteOnlyStrategy.REMOTE_ONLY_STRATEGY)
public class RemoteOnlyStrategy extends AbstractInvocationStrategy implements IInvocationStrategy, IStatusReporter {

    final Logger logger = LoggerFactory.getLogger(getClass());

    static final String REMOTE_ONLY_STRATEGY = "REMOTE_ONLY";

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    private ClientContext clientContext;

    @Autowired
    private ServiceConfig serviceConfig;

    private IStatusManager statusManager;

    private StatusReport lastStatus;

    public static final String STATUS_NAME = "NETWORK.REMOTE";
    public static final String STATUS_ICON = "cloud";

    private Map<String, Status> statuses = new ConcurrentHashMap<>();

    public String getStrategyName() {
        return REMOTE_ONLY_STRATEGY;
    }

    public void setServiceConfig(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    @Override
    public Object invoke(List<String> profileIds, Object proxy, Method method, Map<String, Object> endpoints, Object[] args) throws Throwable {

        Throwable lastException = null;
        Object result = null;
        List<ServiceVisit> serviceVisits = new ArrayList<>();

        for (String profileId : profileIds) {
            ServiceVisit serviceVisit = new ServiceVisit();
            serviceVisit.setProfileId(profileId);
            long startTime = System.currentTimeMillis();
            try {
                result = invokeProfile(profileId, serviceConfig.getProfileConfig(profileId), proxy, method, endpoints, args);
                break;
            } catch (Throwable ex) {
                serviceVisit.setException(ex);
                lastException = ex;
                logger.warn(String.format("Remote service %s unavailable.", profileId), ex);
            } finally {
                serviceVisit.setElapsedTimeMillis(System.currentTimeMillis()-startTime);
                serviceVisits.add(serviceVisit);
            }
        }

        if (result != null) {
            populateServiceVisits(result, serviceVisits);
            return result;
        }

        if (lastException != null) {
            ServiceException serviceException = new ServiceException("Failed to invoke remote service(s)", lastException);
            serviceException.setServiceVisits(serviceVisits);
            throw serviceException;
        }

        log.warn("We should not have gotten here - there should be a result or lastException");
        return null;
    }

    private void populateServiceVisits(Object result, List<ServiceVisit> serviceVisits) {
        if (result instanceof ServiceResult) {
            ((ServiceResult) result).setServiceVisits(serviceVisits);
        }
    }

    private Object invokeProfile(String profileId, ProfileConfig profileConfig, Object proxy, Method method,
                                 Map<String, Object> endpoints, Object[] args) throws ResourceAccessException {

        int httpTimeoutInSecond = profileConfig.getHttpTimeout();
        int connectTimeoutInSecond = profileConfig.getConnectTimeout() > 0 ? profileConfig.getConnectTimeout() : httpTimeoutInSecond;
        ConfiguredRestTemplate template = new ConfiguredRestTemplate(httpTimeoutInSecond, connectTimeoutInSecond);

        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        RequestMethod[] requestMethods = mapping.method();

        HttpHeaders headers = new HttpHeaders();

        if (profileConfig.getApiToken() != null) {
            headers.set(REST_API_TOKEN_HEADER_NAME, profileConfig.getApiToken());
        } else {
            logger.warn("missing apiToken for service profile \"{}\"", profileId);
        }

        if( clientContext != null ) {
            clientContext.put("correlationId", UUID.randomUUID().toString());
            for (String propertyName : clientContext.getPropertyNames()) {
                headers.set("ClientContext-" + propertyName, clientContext.get(propertyName));
            }
        }

        if (requestMethods != null && requestMethods.length > 0) {
            try {
                HttpMethod requestMethod = translate(requestMethods[0]);
                String serverUrl = buildUrl(profileConfig, proxy, method, args);
                Object[] newArgs = findArgs(method, args);
                if (isMultiPartUpload(method, args)) {
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    body.add(getRequestParamName(method), new FileSystemResource(getMultiPartFile(method, args)));
                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                    ResponseEntity<String> response = template.postForEntity(serverUrl, requestEntity, String.class, newArgs);
                    if (response.getStatusCode() != HttpStatus.OK) {
                        throw new PosServerException();
                    }
                } else {
                    Object requestBody = findRequestBody(method, args);
                    if (method.getReturnType().equals(Void.TYPE)) {
                        template.execute(serverUrl, requestBody, requestMethod, headers, newArgs);
                    } else {
                        Object result =  template.execute(serverUrl, requestBody, method.getReturnType(), requestMethod, headers, newArgs);
                        statuses.put(profileId, Status.Online);
                        reportStatus();
                        return result;
                    }
                }
            } catch (Throwable ex) {
                statuses.put(profileId, Status.Offline);
                reportStatus(ex.getMessage());
                throw ex;
            }

        } else {
            throw new IllegalStateException("A method must be specified on the @RequestMapping");
        }
        return null;

    }

    private void reportStatus() {
        reportStatus("");
    }

    private void reportStatus(String message) {

        Status lowestCommonDenominatorStatus = Status.Online;

        for (Status status : statuses.values()) {
            if (status == Status.Error || status == Status.Offline) {
                lowestCommonDenominatorStatus = status;
                break;
            }
        }

        this.lastStatus = new StatusReport(STATUS_NAME, STATUS_ICON, lowestCommonDenominatorStatus, message);
        if (this.statusManager != null) {
            this.statusManager.reportStatus(lastStatus);
        }
    }

    protected String buildUrl(ProfileConfig profileConfig, Object proxy, Method method, Object[] args) {
        String url = profileConfig.getUrl();
        String path = buildPath(method);
        url = String.format("%s%s", url, path);
        return url;
    }

    protected boolean isMultiPartUpload(Method method, Object[] args) {
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof MultipartFile) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String getMultiPartFile(Method method, Object[] args) {
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof MultipartFile) {
                    return ((MultipartFile) arg).getName();
                }
            }
        }
        return null;
    }

    protected String getRequestParamName(Method method) {
        Annotation[][] types = method.getParameterAnnotations();
        for (int i = 0; i < types.length; i++) {
            Annotation[] argAnnotations = types[i];
            for (Annotation annotation : argAnnotations) {
                if (RequestParam.class.equals(annotation.annotationType())) {
                    return ((RequestParam) annotation).value();
                }
            }
        }
        return null;
    }

    protected Object[] findArgs(Method method, Object[] args) {
        List<Object> newArgs = new ArrayList<>();
        Annotation[][] types = method.getParameterAnnotations();
        for (int i = 0; i < types.length; i++) {
            Annotation[] argAnnotations = types[i];
            for (Annotation annotation : argAnnotations) {
                if (PathVariable.class.equals(annotation.annotationType()) || RequestParam.class.equals(annotation.annotationType())) {
                    newArgs.add(args[i]);
                }

            }
        }
        return newArgs.toArray();
    }

    protected Object findRequestBody(Method method, Object[] args) {
        Annotation[][] types = method.getParameterAnnotations();
        for (int i = 0; i < types.length; i++) {
            Annotation[] argAnnotations = types[i];
            for (Annotation annotation : argAnnotations) {
                if (RequestBody.class.equals(annotation.annotationType())) {
                    return args[i];
                }

            }
        }

        return null;
    }

    protected HttpMethod translate(RequestMethod method) {
        return HttpMethod.valueOf(method.name());
    }

    @Override
    public StatusReport getStatus(IStatusManager statusManager) {
        this.statusManager = statusManager;
        if (lastStatus == null) {
            this.lastStatus = new StatusReport(STATUS_NAME, STATUS_ICON, Status.Unknown, "");
        }
        return lastStatus;
    }
}
