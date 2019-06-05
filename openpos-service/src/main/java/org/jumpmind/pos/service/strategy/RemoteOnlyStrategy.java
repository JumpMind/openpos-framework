package org.jumpmind.pos.service.strategy;

import org.jumpmind.pos.service.PosServerException;
import org.jumpmind.pos.service.ServiceSpecificConfig;
import org.jumpmind.pos.util.web.ConfiguredRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(RemoteOnlyStrategy.REMOTE_ONLY_STRATEGY)
public class RemoteOnlyStrategy extends AbstractInvocationStrategy implements IInvocationStrategy {

    static final String REMOTE_ONLY_STRATEGY = "REMOTE_ONLY";

    public String getStrategyName() {
        return REMOTE_ONLY_STRATEGY;
    }

    @Override
    public Object invoke(ServiceSpecificConfig config, Object proxy, Method method, Map<String, Object> endpoints, Object[] args) throws Throwable {
        int httpTimeoutInSecond = config.getHttpTimeout();
        ConfiguredRestTemplate template = new ConfiguredRestTemplate(httpTimeoutInSecond);
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        RequestMethod[] requestMethods = mapping.method();
        if (requestMethods != null && requestMethods.length > 0) {
            HttpMethod requestMethod = translate(requestMethods[0]);
            String serverUrl = buildUrl(config, proxy, method, args);
            Object[] newArgs = findArgs(method, args);
            if (isMultiPartUpload(method, args)) {
                HttpHeaders headers = new HttpHeaders();
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
                    template.execute(serverUrl, requestBody, requestMethod, newArgs);
                } else {
                    return template.execute(serverUrl, requestBody, method.getReturnType(), requestMethod, newArgs);
                }
            }
        } else {
            throw new IllegalStateException("A method must be specified on the @RequestMapping");
        }
        return null;
    }

    protected String buildUrl(ServiceSpecificConfig config, Object proxy, Method method, Object[] args) {
        String url = config.getUrl();
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

}
