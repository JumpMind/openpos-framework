package org.jumpmind.pos.devices.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.devices.DeviceNotFoundException;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.ModelId;
import org.jumpmind.pos.persist.Query;
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

    Query<DeviceStatusModel> connectedDevicesQuery = new Query<DeviceStatusModel>().named("connectedDevices").result(DeviceStatusModel.class);

    @Cacheable(value = CACHE_NAME, key = "#deviceId")
    public DeviceModel getDevice(String deviceId) {
        DeviceModel device = devSession.findByNaturalId(DeviceModel.class, new ModelId("deviceId", deviceId));
        if (device != null) {
            device.setDeviceParamModels(getDeviceParams(device.getDeviceId()));
            return device;
        } else {
            DeviceModel virtualDevice = virtualDeviceRepository.getByDeviceId(deviceId);
            if (virtualDevice != null) {
                return virtualDevice;
            }
            throw new DeviceNotFoundException("No device found for deviceId=" + deviceId);
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

    public List<DeviceModel> getPairedDevices(String businessUnitId) {
        return findDevices(businessUnitId)
                .stream()
                .filter(device -> StringUtils.isNotBlank(device.getPairedDeviceId()))
                .collect(Collectors.toList());
    }

    public List<DeviceModel> getPairedDevicesByAppId(String businessUnitId, String appId) {
        return getPairedDevices(businessUnitId).stream()
                .filter(device -> device.getAppId().equals(appId))
                .collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#deviceId"),
            @CacheEvict(value = CACHE_NAME, key = "#pairedDeviceId")
    })
    public void pairDevice(String deviceId, String pairedDeviceId) {
        DeviceModel device = getDevice(deviceId);

        // First unpair an existing paired device
        if (StringUtils.isNotBlank(device.getPairedDeviceId())) {
            unpairDevice(deviceId, device.getPairedDeviceId());
        }

        // Pair device
        device.setPairedDeviceId(pairedDeviceId);
        saveDevice(device);

        // Link paired device to device it's paired with
        DeviceModel pairedDevice = getDevice(pairedDeviceId);
        pairedDevice.setPairedDeviceId(deviceId);
        saveDevice(pairedDevice);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#deviceId"),
            @CacheEvict(value = CACHE_NAME, key = "#pairedDeviceId")
    })
    public void unpairDevice(String deviceId, String pairedDeviceId) {
        // Unpair device
        DeviceModel device = getDevice(deviceId);
        device.setPairedDeviceId(null);
        saveDevice(device);

        // Unlink paired device to device it was paired with
        DeviceModel pairedDevice = getDevice(pairedDeviceId);
        pairedDevice.setPairedDeviceId(null);
        saveDevice(pairedDevice);
    }

    @CacheEvict(value = CACHE_NAME, key = "#deviceId")
    public void setAppId(String deviceId, String newAppId) {
        String deviceAuth = getDeviceAuth(deviceId);
        DeviceModel device = getDeviceByAuth(deviceAuth);

        device.setAppId(newAppId);
        saveDevice(device);
        saveDeviceAuth(deviceId, deviceAuth);
    }

    public String getDeviceAuth(String deviceId) {
        DeviceAuthModel deviceAuthModel = getDeviceAuthModel(deviceId);

        if (deviceAuthModel != null) {
            return deviceAuthModel.getAuthToken();
        } else {
            throw new DeviceNotFoundException();
        }
    }

    public DeviceAuthModel getDeviceAuthModel(String deviceId) {
        return devSession.findByNaturalId(DeviceAuthModel.class, new ModelId("deviceId", deviceId));
    }

    public List<DeviceAuthModel> getDisconnectedDevices(String businessUnitId, String installationId) {
        Map<String, Object> statusParams = new HashMap<>();
        statusParams.put("deviceStatus", DeviceStatusConstants.CONNECTED);
        Set<String> connectedDevices = getConnectedDevices(businessUnitId, installationId);

        Map<String, Object> deviceParams = new HashMap<>();
        deviceParams.put("businessUnitId", businessUnitId);
        final Set<String> devices = devSession.findByFields(DeviceModel.class, deviceParams, 10000).stream().map(DeviceModel::getDeviceId).collect(Collectors.toSet());
        devices.removeAll(connectedDevices);
        return devSession.findAll(DeviceAuthModel.class, 10000).stream().filter(d -> devices.contains(d.getDeviceId())).sorted().collect(Collectors.toList());
    }

    public Set<String> getConnectedDevices(String businessUnitId, String installationId) {
        Map<String, Object> statusParams = new HashMap<>();
        statusParams.put("businessUnitId", businessUnitId);
        statusParams.put("installationId", installationId);
        statusParams.put("deviceStatus", DeviceStatusConstants.CONNECTED);
        List<DeviceStatusModel> deviceStatuses =
                devSession.query(connectedDevicesQuery, statusParams, 10000);
        return deviceStatuses.stream().map(s->s.getDeviceId()).collect(Collectors.toSet());
    }

    public DeviceModel getDeviceByAuth(String auth) {
        Map<String, Object> params = new HashMap<>();
        params.put("authToken", auth);

        DeviceAuthModel authModel = devSession.findFirstByFields(DeviceAuthModel.class, params, 1);

        if (authModel == null) {
            throw new DeviceNotFoundException();
        }

        params = new HashMap<>();
        params.put("deviceId", authModel.getDeviceId());

        DeviceModel deviceModel = devSession.findFirstByFields(DeviceModel.class, params, 1);

        if (deviceModel == null) {
            throw new DeviceNotFoundException();
        }

        deviceModel.setDeviceParamModels(getDeviceParams(deviceModel.getDeviceId()));

        return deviceModel;
    }

    @CacheEvict(value = CACHE_NAME, key = "#device.deviceId")
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

    public void saveDeviceAuth(String deviceId, String authToken) {

        DeviceAuthModel authModel = new DeviceAuthModel();
        authModel.setDeviceId(deviceId);
        authModel.setAuthToken(authToken);

        devSession.save(authModel);
    }

    public DevicePersonalizationModel findDevicePersonalizationModel(String deviceName) {
        final DevicePersonalizationModel model = devSession.findByNaturalId(DevicePersonalizationModel.class, new ModelId("deviceName", deviceName));
        List<DeviceParamModel> params = getDeviceParams(model.getDeviceId());

        if (params == null) {
            params = new ArrayList<>();
        }

        model.setDeviceParamModels(params);

        return model;
    }
  
    private List<DeviceParamModel> getDeviceParams(String deviceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", deviceId);

        return devSession.findByFields(DeviceParamModel.class, params, 10000);
    }

    public void updateDeviceStatus(String deviceId, String status) {
        DeviceStatusModel statusModel = devSession.findByNaturalId(DeviceStatusModel.class,
                ModelId.builder().key("deviceId", deviceId).build());
        if (statusModel == null || !statusModel.getDeviceId().equals(deviceId)) {
            statusModel = DeviceStatusModel.builder().deviceId(deviceId).build();
        }
        statusModel.setDeviceStatus(status);
        statusModel.setLastUpdateTime(new Date());
        devSession.save(statusModel);
    }

}
