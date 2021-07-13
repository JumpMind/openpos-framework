package org.jumpmind.pos.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.jumpmind.pos.util.RestApiSupport.REST_API_CONTEXT_PATH;

public abstract class AbstractServiceMockRequestBuilder {

    protected String url;
    protected Object content;
    protected String deviceId;
    protected String appId;
    protected ObjectMapper mapper;

    public AbstractServiceMockRequestBuilder (String url){
        this.url = url.startsWith(REST_API_CONTEXT_PATH) ? url : REST_API_CONTEXT_PATH + url;
        this.mapper = new ObjectMapper();
    }

    public AbstractServiceMockRequestBuilder content(Object content){
        this.content = content;
        return this;
    }

    public AbstractServiceMockRequestBuilder deviceId(String deviceId){
        this.deviceId = deviceId;
        return this;
    }

    public AbstractServiceMockRequestBuilder appId(String appId){
        this.appId = appId;
        return this;
    }

    public AbstractServiceMockRequestBuilder mapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    public abstract MockHttpServletRequestBuilder build() throws JsonProcessingException;

    protected void appendBaseParameters(MockHttpServletRequestBuilder request ) throws JsonProcessingException {
        request.contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .content( mapper.writerWithDefaultPrettyPrinter().writeValueAsString(content));

        if( deviceId != null ) {
            request.header("ClientContext-deviceId", deviceId);
        }

        if( appId != null ) {
            request.header("ClientContext-appId", appId);
        }

    }
}
