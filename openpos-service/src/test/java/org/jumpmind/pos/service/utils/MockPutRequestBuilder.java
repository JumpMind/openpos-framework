package org.jumpmind.pos.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class MockPutRequestBuilder extends AbstractServiceMockRequestBuilder {
    public MockPutRequestBuilder(String url) {
        super(url);
    }

    @Override
    public MockHttpServletRequestBuilder build() throws JsonProcessingException {
        MockHttpServletRequestBuilder request = put(url);
        appendBaseParameters(request);

        return request;
    }
}
