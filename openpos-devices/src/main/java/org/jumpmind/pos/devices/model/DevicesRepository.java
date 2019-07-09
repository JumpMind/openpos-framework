package org.jumpmind.pos.devices.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.devices.DevicesUtils;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@DependsOn(value = { "DevicesModule" })
public class DevicesRepository {

    @Autowired
    @Qualifier("devSession")
    @Lazy
    DBSession dbSession;
    
    @Autowired(required=false)
    List<IDeviceModelPostProcessor> postProcessors;

    Query<DeviceConfigPropModel> devicePropsQuery = new Query<DeviceConfigPropModel>().named("devicePropsLookupByProfile").result(DeviceConfigPropModel.class);

    public Map<String, DeviceConfigModel> getDevices(int maxResults) {
        Map<String, DeviceConfigModel> byLogicalName = new HashMap<>();
        List<DeviceConfigModel> devices = dbSession.findAll(DeviceConfigModel.class, maxResults);
        List<DeviceConfigPropModel> properties = dbSession.findAll(DeviceConfigPropModel.class, maxResults);
        for (DeviceConfigModel deviceModel : devices) {
            byLogicalName.put(DevicesUtils.getLogicalName(deviceModel), deviceModel);
        }

        for (DeviceConfigPropModel devicePropsModel : properties) {
            DeviceConfigModel deviceModel = byLogicalName.get(DevicesUtils.getLogicalName(devicePropsModel));
            if (deviceModel != null) {
                deviceModel.add(devicePropsModel);
            }
        }
        
        if (postProcessors != null) {
            for (IDeviceModelPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcess(byLogicalName);
            }
        }

        return byLogicalName;
    }

}
