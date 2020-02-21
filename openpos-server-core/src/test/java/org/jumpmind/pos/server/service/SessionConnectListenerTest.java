package org.jumpmind.pos.server.service;

import org.jumpmind.pos.devices.DeviceNotAuthorizedException;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DeviceParamModel;
import org.jumpmind.pos.devices.service.IDevicesService;
import org.jumpmind.pos.devices.service.model.AuthenticateDeviceResponse;
import org.jumpmind.pos.devices.service.model.PersonalizationParameter;
import org.jumpmind.pos.devices.service.model.PersonalizationParameters;
import org.jumpmind.pos.util.clientcontext.ClientContextConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jumpmind.pos.server.config.MessageUtils.COMPATIBILITY_VERSION_HEADER;
import static org.jumpmind.pos.server.config.MessageUtils.QUERY_PARAMS_HEADER;
import static org.junit.Assert.*;

public class SessionConnectListenerTest {

    @Mock
    PersonalizationParameters personalizationParameters;

    @Mock
    ClientContextConfig clientContextConfig;

    @Mock
    IDevicesService devicesService;

    @InjectMocks
    SessionConnectListener sessionConnectListener = new SessionConnectListener();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private SessionConnectEvent makeMockEvent(
            String sessionId,
            String authToken,
            String deviceToken,
            String deviceId,
            String appId,
            String version,
            String compatibilityVersion,
            String queryParams ) {
        SessionConnectEvent mockEvent = Mockito.mock(SessionConnectEvent.class);
        Message mockMessage = Mockito.mock(Message.class);
        MessageHeaders mockHeaders = Mockito.mock(MessageHeaders.class);

        Mockito.when(mockEvent.getMessage()).thenReturn(mockMessage);
        Mockito.when(mockMessage.getHeaders()).thenReturn(mockHeaders);

        Mockito.when(mockHeaders.get("simpSessionId")).thenReturn(sessionId);

        Map<String, List<String>> nativeHeaders = new HashMap<>();
        nativeHeaders.put("authToken", Arrays.asList(authToken));
        nativeHeaders.put("deviceToken", Arrays.asList(deviceToken));
        nativeHeaders.put("version", Arrays.asList(version));
        nativeHeaders.put("deviceId", Arrays.asList(deviceId));
        nativeHeaders.put("appId", Arrays.asList(appId));
        nativeHeaders.put(COMPATIBILITY_VERSION_HEADER, Arrays.asList(compatibilityVersion));
        nativeHeaders.put(QUERY_PARAMS_HEADER, Arrays.asList(queryParams));

        Mockito.when(mockHeaders.get("nativeHeaders")).thenReturn(nativeHeaders);
        return mockEvent;
    }

    @Test
    public void onApplicationEventShouldAuthenticateDeviceAndSaveDeviceModel() {

        Mockito.when(devicesService.authenticateDevice(Mockito.any())).thenReturn(
                AuthenticateDeviceResponse.builder()
                        .deviceModel( new DeviceModel() )
                        .build()
        );

        sessionConnectListener.onApplicationEvent(makeMockEvent("123456", null, null, null, null, null, null, null));

        assertNotNull(sessionConnectListener.getDeviceModel("123456"));
    }

    @Test
    public void onApplicationEventShouldAuthenticateSession() {
        Mockito.when(devicesService.authenticateDevice(Mockito.any())).thenReturn(
                AuthenticateDeviceResponse.builder()
                        .deviceModel( new DeviceModel() )
                        .build()
        );

        sessionConnectListener.onApplicationEvent(makeMockEvent("123456", null, "MY_TOKEN", null, null, null, null, null));

        assertTrue(sessionConnectListener.isSessionAuthenticated("123456"));
    }

    @Test( expected = DeviceNotAuthorizedException.class )
    public void onApplicationEventShouldNotAuthenticateSessionIfDeviceNotAuthenticated() {
        Mockito.when(devicesService.authenticateDevice(Mockito.any())).thenThrow(new DeviceNotAuthorizedException());

        sessionConnectListener.onApplicationEvent(makeMockEvent("123456", null, null, null, null,null, null, null));
        assertFalse(sessionConnectListener.isSessionAuthenticated("123456"));
    }

    @Test
    public void onApplicationEventShouldNotAuthIfServerTokensDontMatch() {
        Mockito.when(devicesService.authenticateDevice(Mockito.any())).thenReturn(
                AuthenticateDeviceResponse.builder()
                        .deviceModel( new DeviceModel() )
                        .build()
        );
        ReflectionTestUtils.setField(sessionConnectListener, "serverAuthToken", "0987");

        sessionConnectListener.onApplicationEvent(makeMockEvent("123456", "1234", "MY_TOKEN", null, null, null, null, null));

        assertFalse(sessionConnectListener.isSessionAuthenticated("123456"));
    }

    @Test
    public void onApplicationEventShouldGetClientContextHeaders() {
        Mockito.when(devicesService.authenticateDevice(Mockito.any())).thenReturn(
                AuthenticateDeviceResponse.builder()
                        .deviceModel( new DeviceModel() )
                        .build()
        );

        Mockito.when(clientContextConfig.getParameters()).thenReturn(Arrays.asList("deviceId"));

        SessionConnectEvent mockEvent = makeMockEvent(
                "123456",
                null,
                null,
                "11111-111",
                "pos",
                null,
                null,
                null );

        sessionConnectListener.onApplicationEvent(mockEvent);

        assertEquals("11111-111", sessionConnectListener.getClientContext("123456").get("deviceId"));
        assertFalse(sessionConnectListener.getClientContext("123456").containsKey("appId"));
    }

    @Test
    public void onApplicationEventShouldGetPersonalizationParamsFromDeviceModel() {
        DeviceModel testDevice = new DeviceModel();
        testDevice.setDeviceParamModels(Arrays.asList( DeviceParamModel.builder().paramName("brandId").paramValue("NU").build()));

        Mockito.when(devicesService.authenticateDevice(Mockito.any())).thenReturn(
                AuthenticateDeviceResponse.builder()
                        .deviceModel( testDevice )
                        .build()
        );

        Mockito.when(personalizationParameters.getParameters()).thenReturn(Arrays.asList(PersonalizationParameter.builder().property("brandId").build()));

        SessionConnectEvent mockEvent = makeMockEvent(
                "123456",
                null,
                null,
                null,
                null,
                null,
                null,
                null );

        sessionConnectListener.onApplicationEvent(mockEvent);

        assertEquals("NU", sessionConnectListener.getPersonalizationResults("123456").get("brandId"));
    }

    @Test
    public void onApplicationEventShouldCheckCompatibility() {
        Mockito.when(devicesService.authenticateDevice(Mockito.any())).thenReturn(
                AuthenticateDeviceResponse.builder()
                        .deviceModel( new DeviceModel() )
                        .build()
        );
        ReflectionTestUtils.setField(sessionConnectListener, "serverCompatibilityVersion", "12");

        sessionConnectListener.onApplicationEvent(makeMockEvent("123456", "1234", "MY_TOKEN", null, null, null, "12", null));

        assertTrue(sessionConnectListener.isSessionCompatible("123456"));
    }

    @Test
    public void onApplicationEventShouldCheckUnCompatibility() {
        Mockito.when(devicesService.authenticateDevice(Mockito.any())).thenReturn(
                AuthenticateDeviceResponse.builder()
                        .deviceModel( new DeviceModel() )
                        .build()
        );
        ReflectionTestUtils.setField(sessionConnectListener, "serverCompatibilityVersion", "1");

        sessionConnectListener.onApplicationEvent(makeMockEvent("123456", "1234", "MY_TOKEN", null, null, null, "12", null));

        assertFalse(sessionConnectListener.isSessionCompatible("123456"));
    }
}
