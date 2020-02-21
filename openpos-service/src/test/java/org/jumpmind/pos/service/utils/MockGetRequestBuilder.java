package org.jumpmind.pos.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class MockGetRequestBuilder extends AbstractServiceMockRequestBuilder {
    public MockGetRequestBuilder(String url) {
        super(url);
    }

    public MockHttpServletRequestBuilder build() throws JsonProcessingException {
        MockHttpServletRequestBuilder request = get(url);
        appendBaseParameters(request);

        return request;
    }
}
