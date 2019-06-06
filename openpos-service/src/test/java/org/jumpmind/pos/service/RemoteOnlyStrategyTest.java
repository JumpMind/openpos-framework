package org.jumpmind.pos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.jumpmind.pos.service.strategy.RemoteOnlyStrategy;
import org.jumpmind.pos.util.model.ErrorResult;
import org.jumpmind.pos.util.web.ConfiguredRestTemplate;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class })
public class RemoteOnlyStrategyTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().notifier(new ConsoleNotifier(true)));

    ObjectMapper mapper = new ConfiguredRestTemplate().getMapper();
    
    RemoteOnlyStrategy handler = new RemoteOnlyStrategy();


    @Test
    public void testInvokeRemotePostWithResponseNoRequest() throws Throwable {
        stubFor(post(urlEqualTo("/check/deviceid/test001/version")).willReturn(status(200).withHeader("Content-Type", "application/json")
                .withBody(mapper.writeValueAsString(new TestResponse(new BigDecimal("1.11"), "abcd")))));

        TestResponse response = (TestResponse) handler.invoke(config(), null, ITestService.class.getMethod("testPost", String.class), null,
                new Object[] { "test001" });

        assertNotNull(response);
        assertEquals(new BigDecimal("1.11"), response.total);
        assertEquals("abcd", response.message);
    }

    @Test
    public void testInvokeRemotePutWithRequestWithResponse() throws Throwable {
        stubFor(put(urlEqualTo("/check/deviceid/test001/yada")).willReturn(status(200).withHeader("Content-Type", "application/json")
                .withBody(mapper.writeValueAsString(new TestResponse(new BigDecimal("3.14"), "xyz")))));

        TestResponse response = (TestResponse) handler.invoke(config(), null, ITestService.class.getMethod("testPut", String.class, TestRequest.class), null,
                new Object[] { "test001", new TestRequest("one", 1) });

        assertNotNull(response);
        assertEquals(new BigDecimal("3.14"), response.total);
        assertEquals("xyz", response.message);
    }

    @Test
    public void testInvokeRemotePutWithNoResponse() throws Throwable {
        stubFor(put(urlEqualTo("/check/deviceid/test001/nuttin")).willReturn(status(200)));

        handler.invoke(config(), null, ITestService.class.getMethod("testPutNuttin", String.class, TestRequest.class), null,
                new Object[] { "test001", new TestRequest("one", 1) });

    }

    @Ignore("I can't make wire mock send 500 and the response body like spring does.  not sure what i'm doing wrong")
    @Test
    public void testInvokeRemotePutWithError() throws Throwable {
        ErrorResult result = new ErrorResult("this was a test", new NullPointerException());
        stubFor(put(urlEqualTo("/check/deviceid/test001/nuttin")).willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withBody(mapper.writeValueAsString(result)).withStatus(501)));
        
        handler.invoke(config(), null, ITestService.class.getMethod("testPutNuttin", String.class, TestRequest.class), null,
                new Object[] { "test001", new TestRequest("one", 1) });

    }

    @Test
    public void testInvokeRemoteGet() throws Throwable {
        stubFor(get(urlEqualTo("/check/getmesomeofthat")).willReturn(status(200).withHeader("Content-Type", "application/json")
                .withBody(mapper.writeValueAsString(new TestResponse(new BigDecimal("3.14"), "xyz")))));

        TestResponse response = (TestResponse) handler.invoke(config(), null, ITestService.class.getMethod("testGet"), null, null);

        assertNotNull(response);
        assertEquals(new BigDecimal("3.14"), response.total);
        assertEquals("xyz", response.message);
    }

    private ServiceSpecificConfig config() {
        ServiceSpecificConfig config = new ServiceSpecificConfig();
        config.setHttpTimeout(30);
        config.setUrl("http://localhost:8080");
        return config;
    }

    static class TestRequest {
        String deviceId;
        int sequenceNumber;

        public TestRequest() {
        }

        public TestRequest(String deviceId, int sequenceNumber) {
            super();
            this.deviceId = deviceId;
            this.sequenceNumber = sequenceNumber;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

    }

    static class TestResponse {
        BigDecimal total;
        String message;

        public TestResponse() {
        }

        public TestResponse(BigDecimal total, String message) {
            super();
            this.total = total;
            this.message = message;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    @RestController("test")
    @RequestMapping("/check")
    interface ITestService {

        @RequestMapping(path = "/deviceid/{deviceid}/version", method = RequestMethod.POST)
        public TestResponse testPost(@PathVariable("deviceid") String deviceId);
        
        @RequestMapping(path = "/deviceid/{deviceid}/yada", method = RequestMethod.PUT)
        public TestResponse testPut(@PathVariable("deviceid") String deviceId, @RequestBody TestRequest request);
        
        @RequestMapping(path = "/deviceid/{deviceid}/nuttin", method = RequestMethod.PUT)
        public void testPutNuttin(@PathVariable("deviceid") String deviceId, @RequestBody TestRequest request);
        
        @RequestMapping(path = "/getmesomeofthat", method = RequestMethod.GET)
        public TestResponse testGet();
    }

}
