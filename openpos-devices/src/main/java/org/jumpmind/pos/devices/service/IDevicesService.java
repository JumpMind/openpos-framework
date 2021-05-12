package org.jumpmind.pos.devices.service;

import io.swagger.annotations.Api;
import org.jumpmind.pos.devices.service.model.*;
import org.jumpmind.pos.util.SuppressMethodLogging;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Devices Service")
@RestController("devices")
@RequestMapping("/devices")
public interface IDevicesService {

    @RequestMapping(path = "/personalizationConfig")
    public PersonalizationConfigResponse getPersonalizationConfig();

    @RequestMapping(path = "/personalize", method = RequestMethod.POST)
    @ResponseBody
    public PersonalizationResponse personalize(@RequestBody PersonalizationRequest request);

    @RequestMapping(path = "/device", method = RequestMethod.POST)
    @ResponseBody
    public GetDeviceResponse getDevice(@RequestBody GetDeviceRequest request);

    @SuppressMethodLogging
    @RequestMapping(path = "/myDevice", method = RequestMethod.GET)
    @ResponseBody
    public GetDeviceResponse getMyDevice();

    @RequestMapping(path = "/authenticate", method = RequestMethod.POST)
    @ResponseBody
    public AuthenticateDeviceResponse authenticateDevice(@RequestBody AuthenticateDeviceRequest request);

    @RequestMapping(path = "/disconnectDevice", method = RequestMethod.POST)
    public void disconnectDevice(@RequestBody DisconnectDeviceRequest request);

    @RequestMapping(path = "/find", method = RequestMethod.POST)
    public FindDevicesResponse findDevices(@RequestBody FindDevicesRequest request);

    @RequestMapping(path = "/unpaired", method = RequestMethod.POST)
    public GetUnpairedDevicesResponse getUnpairedDevices(@RequestBody GetUnpairedDevicesRequest request);

    @RequestMapping(path = "/pair", method = RequestMethod.POST)
    public PairDeviceResponse pairDevice(@RequestBody PairDeviceRequest request);

    @RequestMapping(path = "/unpair", method = RequestMethod.POST)
    public UnpairDeviceResponse unpairDevice(@RequestBody UnpairDeviceRequest request);

    @RequestMapping(path = "/setAppId", method = RequestMethod.POST)
    public SetAppIdResponse setAppId(@RequestBody SetAppIdRequest request);
}
