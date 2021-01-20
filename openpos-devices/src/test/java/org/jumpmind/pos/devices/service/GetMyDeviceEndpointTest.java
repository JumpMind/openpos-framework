package org.jumpmind.pos.devices.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jumpmind.pos.devices.TestDevicesConfig;
import org.jumpmind.pos.devices.service.model.GetDeviceResponse;
import org.jumpmind.pos.test.MockGetRequestBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(classes = { TestDevicesConfig.class })
public class GetMyDeviceEndpointTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    public void getMyDeviceShouldReturnDeviceFromClientContext() throws Exception {

        String result = mvc.perform(new MockGetRequestBuilder("/devices/myDevice").appId("pos").deviceId("00100-001").build())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("00100-001", mapper.readValue(result, GetDeviceResponse.class).getDeviceModel().getDeviceId());
    }

    @Test
    public void getMyDeviceWithNoContext() throws Exception {
        String result = mvc.perform(new MockGetRequestBuilder("/devices/myDevice").build())
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        assertEquals("server", mapper.readValue(result, GetDeviceResponse.class).getDeviceModel().getAppId());
    }
}
