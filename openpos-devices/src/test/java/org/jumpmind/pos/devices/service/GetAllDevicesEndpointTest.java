package org.jumpmind.pos.devices.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jumpmind.pos.devices.TestDevicesConfig;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.devices.service.model.GetAllDevicesResponse;
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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")

@AutoConfigureMockMvc
@ContextConfiguration(classes = { TestDevicesConfig.class })
public class GetAllDevicesEndpointTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mvc;

    @Autowired
    DevicesRepository repository;

    @Test
    public void getDeviceShouldReturnMatchingDevice() throws Exception {

        String result =
            mvc.perform(new MockGetRequestBuilder("/devices/").build())
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        List<DeviceModel> expected = repository.getAllDevices();

        assertEquals(expected.size(), mapper.readValue(result, GetAllDevicesResponse.class).getDevices().size());
    }
}
