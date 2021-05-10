package org.jumpmind.pos.devices.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.ModelId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DevicesRepository {
    private final static String CACHE_NAME = "/devices/device";

    @Autowired
    @Lazy
    DBSession devSession;

    @Autowired
    VirtualDeviceRepository virtualDeviceRepository;

    @Cacheable(value = CACHE_NAME, key = "#deviceId + '-' + #appId")
    public DeviceModel getDevice(String deviceId, String appId) {
        DeviceModel device = devSession.findByNaturalId(DeviceModel.class, new ModelId("deviceId", deviceId, "appId", appId));
        if (device != null) {
            device.setDeviceParamModels(getDeviceParams(device.getDeviceId(), device.getAppId()));
            return device;
        } else {
            DeviceModel virtualDevice = virtualDeviceRepository.getByDeviceIdAppId(deviceId, appId);
            if (virtualDevice != null) {
                return virtualDevice;
            }
            throw new DeviceNotFoundException("No device found for appId=" + appId + " deviceId=" + deviceId);
        }
    }

    public List<DeviceModel> findDevices(String businessUnitId) {
        Map<String, Object> params = new HashMap<>();
        params.put("businessUnitId", businessUnitId);
        return devSession.findByFields(DeviceModel.class, params, 1000);
    }

    public List<DeviceModel> getUnpairedDevices(String businessUnitId) {
        return findDevices(businessUnitId)
                .stream()
                .filter(device -> StringUtils.isBlank(device.getPairedDeviceId()))
                .collect(Collectors.toList());
    }

    public List<DeviceModel> getUnpairedDevicesByAppId(String businessUnitId, String appId) {
        return getUnpairedDevices(businessUnitId).stream()
                .filter(device -> device.getAppId().equals(appId))
                .collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#deviceId + '-' + #appId"),
            @CacheEvict(value = CACHE_NAME, key = "#pairedDeviceId + '-' + #pairedAppId")
    })
    public void pairDevice(String deviceId, String appId, String pairedDeviceId, String pairedAppId) {
        DeviceModel device = getDevice(deviceId, appId);

        // First unpair an existing paired device
        if (StringUtils.isNotBlank(device.getPairedDeviceId())) {
            unpairDevice(deviceId, appId, device.getPairedDeviceId(), pairedAppId);
        }

        // Pair device
        device.setPairedDeviceId(pairedDeviceId);
        saveDevice(device);

        // Link paired device to device it's paired with
        DeviceModel pairedDevice = getDevice(pairedDeviceId, pairedAppId);
        pairedDevice.setPairedDeviceId(deviceId);
        saveDevice(pairedDevice);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#deviceId + '-' + #appId"),
            @CacheEvict(value = CACHE_NAME, key = "#pairedDeviceId + '-' + #pairedAppId")
    })
    public void unpairDevice(String deviceId, String appId, String pairedDeviceId, String pairedAppId) {
        // Unpair device
        DeviceModel device = getDevice(deviceId, appId);
        device.setPairedDeviceId(null);
        saveDevice(device);

        // Unlink paired device to device it was paired with
        DeviceModel pairedDevice = getDevice(pairedDeviceId, pairedAppId);
        pairedDevice.setPairedDeviceId(null);
        saveDevice(pairedDevice);
    }

    public String getDeviceAuth(String deviceId, String appId) {
        DeviceAuthModel deviceAuthModel = devSession.findByNaturalId(DeviceAuthModel.class, new ModelId("deviceId", deviceId, "appId", appId));

        if (deviceAuthModel != null) {
            return deviceAuthModel.getAuthToken();
        } else {
            throw new DeviceNotFoundException();
        }
    }

    public List<DeviceAuthModel> getDisconnectedDevices(String businessUnitId) {
        Map<String, Object> statusParams = new HashMap<>();
        statusParams.put("deviceStatus", DeviceStatusConstants.CONNECTED);
        Set<String> connectedDevices = devSession.findByFields(DeviceStatusModel.class, statusParams, 10000).stream().map(d -> d.getDeviceId() + ":" + d.getAppId()).collect(Collectors.toSet());

        Map<String, Object> deviceParams = new HashMap<>();
        deviceParams.put("businessUnitId", businessUnitId);
        final Set<String> devices = devSession.findByFields(DeviceModel.class, deviceParams, 10000).stream().map(d -> d.getDeviceId() + ":" + d.getAppId()).collect(Collectors.toSet());
        devices.removeAll(connectedDevices);
        return devSession.findAll(DeviceAuthModel.class, 10000).stream().filter(d -> devices.contains(d.getDeviceId() + ":" + d.getAppId())).sorted().collect(Collectors.toList());
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

    @CacheEvict(value = CACHE_NAME, key = "#device.deviceId + '-' + #device.appId")
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

    private List<DeviceParamModel> getDeviceParams(String deviceId, String appId) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", deviceId);
        params.put("appId", appId);

        return devSession.findByFields(DeviceParamModel.class, params, 10000);
    }

    public void updateDeviceStatus(String deviceId, String appId, String status) {
        DeviceStatusModel statusModel = devSession.findByNaturalId(DeviceStatusModel.class,
                ModelId.builder().
                        key("deviceId", deviceId).
                        key("appId", appId).build());
        if (statusModel == null || !statusModel.getDeviceId().equals(deviceId)) {
            statusModel = DeviceStatusModel.builder().deviceId(deviceId).appId(appId).build();
        }
        statusModel.setDeviceStatus(status);
        statusModel.setLastUpdateTime(new Date());
        devSession.save(statusModel);
    }

}
