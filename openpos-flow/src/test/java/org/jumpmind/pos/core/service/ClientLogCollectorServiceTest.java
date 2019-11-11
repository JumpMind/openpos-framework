package org.jumpmind.pos.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ClientLogCollectorService.class )
@WebMvcTest(value = ClientLogCollectorService.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
    "openpos.clientLogCollector.timestampFormat=yyyy_MM_dd HH:mm:ss,SSS",
})
public class ClientLogCollectorServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ClientLogCollectorService clientLogCollector;
    
    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void beforeTest() {
    }
    
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
    
    /**
     * Ensure that openpos.clientLogCollector.timestampFormat is used when present
     * for formatting log timestamps.
     * @throws Exception
     */
    @Test
    public void dateFormatTest() throws Exception {
        Logger oldLogger = clientLogCollector.logger;
        TestingBufferAppender appender = new TestingBufferAppender();
        appender.setLayout(new PatternLayout("%X{timestamp} [%X{deviceId}] %p %m%n")); 
        appender.setThreshold(Level.ALL);
        appender.setImmediateFlush(true);
        org.apache.log4j.Logger.getLogger(ClientLogCollectorService.class).addAppender(appender);
        org.apache.log4j.Logger.getLogger(ClientLogCollectorService.class).setLevel(Level.ALL);

        try {
            Date testDate = new Date();
            Calendar testCal = Calendar.getInstance();
            testCal.setTime(testDate);
            SimpleDateFormat expectedDateFormat = new SimpleDateFormat(clientLogCollector.timestampFormat);
            String expectedDateStr = expectedDateFormat.format(testDate);
            
            ClientLogEntry[] entries = {
                    new ClientLogEntry(ClientLogType.log, testDate, "Message 1~"),
                    new ClientLogEntry(ClientLogType.debug, testDate, "Message 2")
            };
            
            clientLogCollector.clientLogs("foo", "111-22222", Arrays.asList(entries));
            String expectedLogOutput = String.format("%s [111-22222] INFO Message 1~%s%s [111-22222] DEBUG Message 2%s", 
                    expectedDateStr, System.getProperty("line.separator"), 
                    expectedDateStr, System.getProperty("line.separator")
            );
            assertThat(appender.buffer.toString()).isEqualTo(expectedLogOutput);
        } finally {
            clientLogCollector.logger = oldLogger;
            org.apache.log4j.Logger.getLogger(ClientLogCollectorService.class).removeAppender(appender);
        }
        
        
    }
    

}
