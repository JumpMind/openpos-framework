package org.jumpmind.pos.devices.service;

import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.devices.TestDevicesConfig;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.service.model.GetDeviceRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = { TestDevicesConfig.class })
public class GetDeviceEndpointTest {
    @Autowired
    GetDeviceEndpoint getDeviceEndpoint;

    @Test
    public void getDeviceShouldReturnMatchingDevice() {
        DeviceModel deviceModel = getDeviceEndpoint.getDevice(
                GetDeviceRequest.builder()
                    .deviceId("00100-001")
                    .appId("pos")
                .build()).getDeviceModel();

        assertEquals("00100-001", deviceModel.getDeviceId());
    }

    @Test(expected = DeviceNotFoundException.class)
    public void getDeviceShouldThrowDeviceNotFoundException() {
        getDeviceEndpoint.getDevice(
                GetDeviceRequest.builder()
                        .deviceId("11111-111")
                        .appId("pos")
                        .build()
        ).getDeviceModel();
    }
}
