package org.jumpmind.pos.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.internal.serialization.Data;
import com.hazelcast.internal.serialization.SerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import org.jumpmind.pos.util.model.DeviceStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {HazelcastConfig.class, HazelcastEnvConfig.class})
@ActiveProfiles("hazelcast")
public class DeviceStatusStreamSerializerTest {

    @Autowired
    Config hazelCastConfig;



    @Test
    public void testDeviceStatusRoundTrip() {
        DeviceStatus status = new DeviceStatus("11111-111");
        status.setServerId("myserver");
        AppEventStreamSerializerTest.AppEventSubclass evt = new AppEventStreamSerializerTest.AppEventSubclass();
        evt.setNewAttr("bar");
        status.setLatestEvent(evt);
        SomePayload p = new SomePayload();
        p.setDoubleValue(10.0);
        p.setStringValue("foo");
        status.setPayload(p);

        SerializationService ss = new DefaultSerializationServiceBuilder().setConfig(hazelCastConfig.getSerializationConfig()).build();
        Data statusData = ss.toData(status);
        assertNotNull(statusData);

        SerializationService ss2 = new DefaultSerializationServiceBuilder().setConfig(hazelCastConfig.getSerializationConfig()).build();
        Object statusObj = ss2.toObject(statusData);
        assertEquals(status, statusObj);

        DeviceStatus deserializedStatus = (DeviceStatus) statusObj;
        assertEquals(status.getPayload(), deserializedStatus.getPayload());
    }

    @lombok.Data
    public static class SomePayload implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private String stringValue;
        private Double doubleValue;

    }
}
