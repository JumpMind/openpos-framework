package org.jumpmind.pos.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.internal.serialization.Data;
import com.hazelcast.internal.serialization.SerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import org.jumpmind.pos.util.event.AppEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {HazelcastConfig.class, HazelcastEnvConfig.class})
@ActiveProfiles("hazelcast")
public class AppEventStreamSerializerTest {

    @Autowired
    Config hazelCastConfig;



    @Test
    public void testAppEventRoundTrip() {
        AppEvent event = new AppEvent();
        event.setRemote(true);
        event.setAppId("pos");
        event.setDeviceId("11111-111");

        SerializationService ss = new DefaultSerializationServiceBuilder().setConfig(hazelCastConfig.getSerializationConfig()).build();
        Data appEventData = ss.toData(event);
        assertNotNull(appEventData);

        SerializationService ss2 = new DefaultSerializationServiceBuilder().setConfig(hazelCastConfig.getSerializationConfig()).build();
        Object appEventObj = ss2.toObject(appEventData);
        assertEquals(event, appEventObj);
    }

    @Test
    public void testAppEventSubclassRoundTrip() {
        AppEventSubclass event = new AppEventSubclass();
        event.setRemote(true);
        event.setAppId("pos");
        event.setDeviceId("11111-111");
        event.setNewAttr("foo");

        SerializationService ss = new DefaultSerializationServiceBuilder().setConfig(hazelCastConfig.getSerializationConfig()).build();
        Data appEventData = ss.toData(event);
        assertNotNull(appEventData);

        SerializationService ss2 = new DefaultSerializationServiceBuilder().setConfig(hazelCastConfig.getSerializationConfig()).build();
        Object appEventObj = ss2.toObject(appEventData);
        assertEquals(event, appEventObj);
    }

    @lombok.Data
    public static class AppEventSubclass extends AppEvent {
        private static final long serialVersionUID = 1L;

        String newAttr;

        public String getNewAttr() {
            return newAttr;
        }

        public void setNewAttr(String newAttr) {
            this.newAttr = newAttr;
        }


    }
}
