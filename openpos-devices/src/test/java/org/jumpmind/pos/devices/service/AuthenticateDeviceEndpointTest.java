package org.jumpmind.pos.devices.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jumpmind.pos.devices.TestDevicesConfig;
import org.jumpmind.pos.devices.service.model.AuthenticateDeviceRequest;
import org.jumpmind.pos.devices.service.model.AuthenticateDeviceResponse;
import org.jumpmind.pos.test.MockPostRequestBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(classes = { TestDevicesConfig.class })
public class AuthenticateDeviceEndpointTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mvc;

    @Test
    public void authenticateDeviceShouldReturnDeviceModel() throws Exception{
        String result = mvc.perform(
                new MockPostRequestBuilder("/devices/authenticate")
                        .content(
                                AuthenticateDeviceRequest.builder()
                                .authToken("123456789")
                                .build()
                        )
                        .build()
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthenticateDeviceResponse response = mapper.readValue(result, AuthenticateDeviceResponse.class);
        assertNotNull(response.getDeviceModel());
    }

    @Test
    public void authenticatedDeviceInvalidAuthShouldThrowError() throws Exception{
        String result = mvc.perform(
                new MockPostRequestBuilder("/devices/authenticate")
                        .content(
                                AuthenticateDeviceRequest.builder()
                                        .authToken("foo")
                                        .build()
                        )
                        .build()
        )
                .andExpect(status().is5xxServerError())
                .andReturn().getResponse().getContentAsString();
    }
}
