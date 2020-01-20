package org.jumpmind.pos.devices.service;

import io.swagger.annotations.Api;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.service.model.PersonalizationRequest;
import org.jumpmind.pos.devices.service.model.PersonalizationResponse;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Devices Service")
@RestController("devices")
@RequestMapping("/devices")
public interface IDevicesService {

    @RequestMapping(path="/personalize", method = RequestMethod.POST)
    @ResponseBody
    public PersonalizationResponse personalize(@RequestBody PersonalizationRequest request);

    @RequestMapping(path="/device/{deviceId}", method = RequestMethod.GET)
    @ResponseBody
    public DeviceModel getDevice(@PathVariable("deviceId") String deviceId);

    @RequestMapping(path="/device", method = RequestMethod.PUT)
    public void saveDevice(@RequestBody DeviceModel device);
}
