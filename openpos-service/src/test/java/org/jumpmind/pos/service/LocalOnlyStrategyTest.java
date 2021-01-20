package org.jumpmind.pos.service;

import static org.junit.Assert.assertEquals;

import org.jumpmind.pos.service.TestServiceConfig.Proxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class })
public class LocalOnlyStrategyTest {

    @Autowired
    EndpointInvoker dispatcher;

    @Autowired
    TestEndpoint endpoint;

    @Autowired
    TestEndpointOverride override;

    @Test
    public void testThatOverrideIsCalled() throws Throwable {
        assertEquals(0, endpoint.invokeCount);
        assertEquals(0, override.invokeCount);
        dispatcher.invoke(null, Proxy.class.getMethod("test"), null);
        assertEquals(0, endpoint.invokeCount);
        assertEquals(1, override.invokeCount);
    }



}
