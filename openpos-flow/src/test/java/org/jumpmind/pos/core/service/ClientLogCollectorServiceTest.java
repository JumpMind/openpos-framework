package org.jumpmind.pos.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ClientLogCollectorService.class )
@WebMvcTest(value = ClientLogCollectorService.class)
public class ClientLogCollectorServiceTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();


    @Test
    public void whenPostclientLogs_returnOk() throws Exception {
        ClientLogEntry[] entries = {
                new ClientLogEntry(ClientLogType.log, new Date(), "Some log message"),
                new ClientLogEntry(ClientLogType.debug, new Date(), "Some debug message")

        };

        mockMvc.perform(
                post("/api/appId/pos/deviceId/111-11111/clientlogs")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(entries))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
