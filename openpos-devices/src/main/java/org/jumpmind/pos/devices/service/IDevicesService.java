package org.jumpmind.pos.devices.service;

import io.swagger.annotations.Api;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.service.model.*;
import org.springframework.web.bind.annotation.*;

import static org.jumpmind.pos.util.RestApiSupport.REST_API_CONTEXT_PATH;

@Api(tags = "Devices Service")
@RestController("devices")
@RequestMapping(REST_API_CONTEXT_PATH + "/devices")
public interface IDevicesService {

    @RequestMapping(path="/", method = RequestMethod.GET)
    @ResponseBody
    public GetAllDevicesResponse getAllDevices();

    @RequestMapping(path="/personalizationConfig")
    public PersonalizationConfigResponse getPersonalizationConfig();

    @RequestMapping(path="/personalize", method = RequestMethod.POST)
    @ResponseBody
    public PersonalizationResponse personalize(@RequestBody PersonalizationRequest request);

    @RequestMapping(path="/device", method = RequestMethod.GET)
    @ResponseBody
    public GetDeviceResponse getDevice(@RequestBody GetDeviceRequest request);

    @RequestMapping(path="/myDevice", method = RequestMethod.GET)
    @ResponseBody
    public GetDeviceResponse getMyDevice();

    @RequestMapping(path="/authenticate", method = RequestMethod.GET)
    @ResponseBody
    public AuthenticateDeviceResponse authenticateDevice(@RequestBody AuthenticateDeviceRequest request);
}
