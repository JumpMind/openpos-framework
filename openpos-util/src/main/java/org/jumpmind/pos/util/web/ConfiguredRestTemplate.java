package org.jumpmind.pos.util.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.util.DefaultObjectMapper;
import org.jumpmind.pos.util.model.ErrorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfiguredRestTemplate extends RestTemplate {

    ObjectMapper mapper;

    static BufferingClientHttpRequestFactory build(int timeout) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(timeout * 1000);
        httpRequestFactory.setConnectTimeout(timeout * 1000);
        httpRequestFactory.setReadTimeout(timeout * 1000);
        return new BufferingClientHttpRequestFactory(httpRequestFactory);
    }

    public ConfiguredRestTemplate() {
        this(30);
    }

    public ConfiguredRestTemplate(int timeout) {
        super(build(timeout));
        this.mapper = DefaultObjectMapper.build();
        getMessageConverters().add(0, new MappingJackson2HttpMessageConverter(this.mapper));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        setInterceptors(interceptors);
        setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
                    ErrorResult result = mapper.readValue(response.getBody(), ErrorResult.class);
                    Throwable serverError = result.getThrowable();
                    String serverMessage = result.getMessage();
                    if (serverError != null && serverError instanceof RuntimeException) {
                        throw (RuntimeException) serverError;
                    } else if (serverMessage != null) {
                        throw new ServerException(serverMessage, serverError);
                    } else {
                        super.handleError(response);
                    }
                } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {

                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new NotFoundException();
                    } else {
                        super.handleError(response);
                    }

                }
            }
        });
    }

    public void execute(String url, Object request, HttpMethod method, Object... args) {
        execute(url, buildRequestEntity(request), Void.class, method, args);
    }

    public <T> T execute(String url, Object request, Class<T> responseClass, HttpMethod method, Object... args) {
        return exchange(url, method, buildRequestEntity(request), responseClass, args).getBody();
    }

    public <T> HttpEntity<T> buildRequestEntity(T request) {
        return new HttpEntity<T>(request, buildHeaders());
    }

    public HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

}

class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    final static Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class.getPackage().getName() + ".REST");

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("===========================request begin================================================");
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        log.info("Headers     : {}", request.getHeaders());
        if (!request.getURI().getPath().contains("/logs/upload")) {
            log.info("Request body: {}", new String(body, "UTF-8"));
        }
        log.info("==========================request end================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        log.info("============================response begin==========================================");
        log.info("Status code  : {}", response.getStatusCode());
        log.info("Status text  : {}", response.getStatusText());
        log.info("Headers      : {}", response.getHeaders());
        log.info("Response body: {}", inputStringBuilder.toString());
        log.info("=======================response end=================================================");
    }

}
