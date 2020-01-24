package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.DeviceTestUtils;
import org.jumpmind.pos.devices.TestDevicesConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = { TestDevicesConfig.class })
public class AuthenticateDeviceEndpointTest {

    @Autowired
    AuthenticateDeviceEndpoint authenticateDeviceEndpoint;

    @Autowired
    DeviceTestUtils deviceTestUtils;

}
