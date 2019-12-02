package org.jumpmind.pos.management;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@ComponentScan
@WebMvcTest(DiscoveryServiceController.class)
@ActiveProfiles(profiles = "localtest")
@AutoConfigureWebClient
public class DiscoveryServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    OpenposManagementServerConfig config;
    
    public void setUp() throws Exception {
    }

    @Ignore
    @Test
    public void testEmptyDeviceId() throws Exception {
        mockMvc.perform(get("/discover"))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPing() throws Exception {
        mockMvc.perform(get("/ping"))
            .andExpect(content().json("{ \"pong\": \"true\" }"));
    }
    @Test
    public void testPersonalize() throws Exception {
        ResultActions resultAction = mockMvc.perform(get("/personalize"))
            .andDo(print())
            .andExpect(status().isOk());
        
        MvcResult result = resultAction.andReturn();
        String content = result.getResponse().getContentAsString();
        ImpersonalizationResponse personalizeResponse = objectMapper.readValue(content, ImpersonalizationResponse.class);
        assertTrue(personalizeResponse.isOpenposManagementServer());
        assertEquals(config.getDevicePattern(), personalizeResponse.getDevicePattern());
    }

    
}
