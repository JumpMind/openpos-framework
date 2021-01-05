package org.jumpmind.pos.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class MockPostRequestBuilder extends  AbstractServiceMockRequestBuilder {
    public MockPostRequestBuilder(String url) {
        super(url);
    }

    @Override
    public MockHttpServletRequestBuilder build() throws JsonProcessingException {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(url);
        appendBaseParameters(request);

        return request;
    }
}
