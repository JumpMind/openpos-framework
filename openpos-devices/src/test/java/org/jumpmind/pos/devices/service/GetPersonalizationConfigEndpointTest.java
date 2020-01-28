package org.jumpmind.pos.devices.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jumpmind.pos.devices.TestDevicesConfig;
import org.jumpmind.pos.devices.service.model.PersonalizationConfigResponse;
import org.jumpmind.pos.devices.service.model.PersonalizationParameters;
import org.jumpmind.pos.service.utils.MockGetRequestBuilder;
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
public class GetPersonalizationConfigEndpointTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PersonalizationParameters personalizationParameters;

    @Test
    public void getPersonalizationConfigShouldReturnDevicePatternAndParams() throws Exception {
        String result = mvc.perform(
                new MockGetRequestBuilder("/devices/personalizationConfig").build()
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PersonalizationConfigResponse response = mapper.readValue(result, PersonalizationConfigResponse.class);
        assertEquals("\\d{5}-\\d{3}", response.getDevicePattern());
        assertEquals(2, response.getParameters().size());
    }
}
