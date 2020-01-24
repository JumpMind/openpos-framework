package org.jumpmind.pos.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class MockPostRequestBuilder extends  AbstractServiceMockRequestBuilder {
    public MockPostRequestBuilder(String url) {
        super(url);
    }

    @Override
    public MockHttpServletRequestBuilder build() throws JsonProcessingException {
        MockHttpServletRequestBuilder request = post(url);
        appendBaseParameters(request);

        return request;
    }
}
