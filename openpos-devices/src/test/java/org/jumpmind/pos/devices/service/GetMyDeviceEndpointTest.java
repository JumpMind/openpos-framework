package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.DeviceTestUtils;
import org.jumpmind.pos.devices.TestDevicesConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(classes = { TestDevicesConfig.class })
public class GetMyDeviceEndpointTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    DeviceTestUtils deviceTestUtils;

    @Before
    public void before(){
        deviceTestUtils.reset();
    }

    @Test
    public void getMyDeviceShouldReturnDeviceFromClientContext() throws Exception {

        mvc.perform(get("/devices/myDevice")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("ClientContext-deviceId", "00100-001")
                .header( "ClientContext-appId", "pos"))
                .andExpect(status().isOk());

    }
}
