package org.jumpmind.pos.devices.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.ModelId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class DevicesRepository {

    @Autowired
    @Lazy
    DBSession devSession;

    @Cacheable("/devices/device")
    public DeviceModel getDevice(String deviceId, String appId) {
        DeviceModel device = devSession.findByNaturalId(DeviceModel.class, new ModelId("deviceId", deviceId, "appId", appId));
        if (device != null) {
            device.setDeviceParamModels(getDeviceParams(device.getDeviceId(), device.getAppId()));
            return device;
        } else {
            throw new DeviceNotFoundException("No device found for appId=" + appId + " deviceId=" + deviceId);
        }
    }

    public String getDeviceAuth(String deviceId, String appId) {
        DeviceAuthModel deviceAuthModel = devSession.findByNaturalId(DeviceAuthModel.class, new ModelId("deviceId", deviceId, "appId", appId));

        if (deviceAuthModel != null) {
            return deviceAuthModel.getAuthToken();
        } else {
            throw new DeviceNotFoundException();
        }
    }

    public DeviceModel getDeviceByAuth(String auth) {
        Map<String, Object> params = new HashMap();
        params.put("authToken", auth);

        DeviceAuthModel authModel = devSession.findFirstByFields(DeviceAuthModel.class, params, 1);

        if (authModel == null) {
            throw new DeviceNotFoundException();
        }

        params = new HashMap<>();
        params.put("deviceId", authModel.getDeviceId());
        params.put("appId", authModel.getAppId());

        DeviceModel deviceModel = devSession.findFirstByFields(DeviceModel.class, params, 1);

        if (deviceModel == null) {
            throw new DeviceNotFoundException();
        }

        deviceModel.setDeviceParamModels(getDeviceParams(deviceModel.getDeviceId(), deviceModel.getAppId()));

        return deviceModel;
    }

    @CacheEvict(value = "/devices/device")
    public void saveDevice(DeviceModel device) {

        devSession.save(device);

        if (CollectionUtils.isNotEmpty(device.getDeviceParamModels())) {
            for (DeviceParamModel paramModel : device.getDeviceParamModels()) {
                paramModel.setAppId(device.getAppId());
                paramModel.setDeviceId(device.getDeviceId());
                devSession.save(paramModel);
            }
        }
    }

    public void saveDeviceAuth(String appId, String deviceId, String authToken) {

        DeviceAuthModel authModel = new DeviceAuthModel();
        authModel.setAppId(appId);
        authModel.setDeviceId(deviceId);
        authModel.setAuthToken(authToken);

        devSession.save(authModel);
    }

    public DevicePersonalizationModel findDevicePersonalizationModel(String deviceName) {
        return devSession.findByNaturalId(DevicePersonalizationModel.class, new ModelId("deviceName", deviceName));
    }

    private List<DeviceParamModel> getDeviceParams(String deviceId, String appId) {
        Map<String, Object> params = new HashedMap();
        params.put("deviceId", deviceId);
        params.put("appId", appId);

        return devSession.findByFields(DeviceParamModel.class, params, 10000);
    }

}
