package org.jumpmind.pos.service.strategy;

import org.jumpmind.pos.service.PosServerException;
import org.jumpmind.pos.service.ProfileConfig;
import org.jumpmind.pos.service.ServiceConfig;
import org.jumpmind.pos.service.ServiceSpecificConfig;
import org.jumpmind.pos.util.clientcontext.ClientContext;
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
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public String getStrategyName() {
        return REMOTE_ONLY_STRATEGY;
    }

    public void setServiceConfig(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    @Override
    public Object invoke(ServiceSpecificConfig config, Object proxy, Method method, Map<String, Object> endpoints, Object[] args) throws Throwable {

        ResourceAccessException lastException=null;
        for (String profileId : config.getProfileIds()) {
            try {
                return invokeProfile(serviceConfig.getProfiles().get(profileId), proxy, method, endpoints, args);
            } catch (ResourceAccessException ex) {
                lastException = ex;
                logger.warn(String.format("Remote service %s unavailable.",profileId));
            }
        }
        throw lastException;
    }

    private Object invokeProfile(ProfileConfig profileConfig, Object proxy, Method method,
                                 Map<String, Object> endpoints, Object[] args) throws ResourceAccessException {

        int httpTimeoutInSecond = profileConfig.getHttpTimeout();
        ConfiguredRestTemplate template = new ConfiguredRestTemplate(httpTimeoutInSecond);
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        RequestMethod[] requestMethods = mapping.method();

        HttpHeaders headers = new HttpHeaders();

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
                        reportStatus(Status.Online);
                        return result;
                    }
                }
            } catch (Throwable ex) {
                reportStatus(Status.Offline, ex.getMessage());
                throw ex;
            }

        } else {
            throw new IllegalStateException("A method must be specified on the @RequestMapping");
        }
        return null;

    }

    private void reportStatus(Status status) {
        reportStatus(status, "");
    }
    private void reportStatus(Status status, String message) {
        this.lastStatus = new StatusReport(STATUS_NAME, STATUS_ICON, status, message);
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
