package org.jumpmind.pos.devices.model;

import org.jumpmind.pos.devices.TestDevicesConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = { TestDevicesConfig.class })
public class DevicesRepositoryTest {

    @Autowired
    DevicesRepository devicesRepository;

    @Test
    public void testFindDevice() {
        DeviceModel device = devicesRepository.getDevice("00100-001");
        assertNotNull(device);
        assertEquals("00100-001", device.getDeviceId());
        assertEquals("Store 100 Register 1", device.getDescription());
        assertEquals("N_AMERICA", device.getTagValue("REGION"));
        assertEquals("US", device.getTagValue("COUNTRY"));
        assertEquals("OH", device.getTagValue("STATE"));
        assertEquals("100", device.getTagValue("STORE_NUMBER"));
        assertEquals("REGULAR", device.getTagValue("STORE_TYPE"));
        assertEquals("WORKSTATION", device.getTagValue("APP_PROFILE"));
        assertEquals("Metl", device.getTagValue("PRICE_ZONE"));
    }
}
