package org.jumpmind.pos.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class MockPutRequestBuilder extends AbstractServiceMockRequestBuilder {
    public MockPutRequestBuilder(String url) {
        super(url);
    }

    @Override
    public MockHttpServletRequestBuilder build() throws JsonProcessingException {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(url);
        appendBaseParameters(request);

        return request;
    }
}
