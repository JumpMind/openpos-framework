package org.jumpmind.pos.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class MockGetRequestBuilder extends AbstractServiceMockRequestBuilder {
    public MockGetRequestBuilder(String url) {
        super(url);
    }

    public MockHttpServletRequestBuilder build() throws JsonProcessingException {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(url);
        appendBaseParameters(request);

        return request;
    }
}
