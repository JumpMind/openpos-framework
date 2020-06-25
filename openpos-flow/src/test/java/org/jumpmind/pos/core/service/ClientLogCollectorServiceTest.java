package org.jumpmind.pos.core.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
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
        ch.qos.logback.classic.Logger clientLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ClientLogCollectorService.class);

        final java.util.List<ILoggingEvent> results = new ArrayList<>();

        AppenderBase testingAppender = new AppenderBase<ILoggingEvent>() {
            @Override
            protected void append(ILoggingEvent eventObject) {
                results.add(eventObject);
            }
        };

        testingAppender.start();
        testingAppender.setName("TESTING");

        clientLogger.addAppender(testingAppender);
        clientLogger.setLevel(Level.ALL);

        Date testDate1 = new Date(System.currentTimeMillis()-10);
        Date testDate2 = new Date(System.currentTimeMillis());

        SimpleDateFormat expectedDateFormat = new SimpleDateFormat(clientLogCollector.timestampFormat);
        String expectedDateStr1 = expectedDateFormat.format(testDate1);
        String expectedDateStr2 = expectedDateFormat.format(testDate2);

        ClientLogEntry[] entries = {
                new ClientLogEntry(ClientLogType.log, testDate1, "Message 1~"),
                new ClientLogEntry(ClientLogType.debug, testDate2, "Message 2")
        };

        clientLogCollector.clientLogs("foo", "111-22222", Arrays.asList(entries));

        System.out.println(results);
        assertThat(results.get(0).getMessage()).isEqualTo("Message 1~");
        assertThat(results.get(0).getMDCPropertyMap().get("timestamp")).isEqualTo(expectedDateStr1);

        assertThat(results.get(1).getMessage()).isEqualTo("Message 2");
        assertThat(results.get(1).getMDCPropertyMap().get("timestamp")).isEqualTo(expectedDateStr2);
    }
    

}
