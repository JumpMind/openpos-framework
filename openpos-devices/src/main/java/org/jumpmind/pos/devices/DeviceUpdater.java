package org.jumpmind.pos.devices;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.devices.model.DeviceModel;
import org.jumpmind.pos.devices.model.DevicesRepository;
import org.jumpmind.pos.persist.ITagProvider;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.jumpmind.pos.util.event.DeviceConnectedEvent;
import org.jumpmind.util.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
@Component
public class DeviceUpdater implements ApplicationListener<DeviceConnectedEvent> {

    @Autowired
    DevicesRepository devicesRepository;

    @Value("${openpos.businessunitId:undefined}")
    String businessUnitId;

    @Value("${openpos.installationId:'not set'}")
    String installationId;

    @Autowired(required = false)
    List<ITagProvider> tagProviders;

    @Autowired
    Environment env;

    @Autowired(required = false)
    CacheManager cacheManager;

    @Autowired
    ClientContext clientContext;

    synchronized public void updateDevice(DeviceModel deviceModel) {
        deviceModel.setTimezoneOffset(clientContext.get("timezoneOffset"));
        deviceModel.setBusinessUnitId(businessUnitId);
        deviceModel.setInstallationId(installationId);
        // TODO check properties also before using default
        deviceModel.setLocale(Locale.getDefault().toString());
        deviceModel.setLastUpdateTime(new Date());
        deviceModel.setLastUpdateBy("personalization");
        deviceModel.updateTags((AbstractEnvironment) env);

        if (this.tagProviders != null && tagProviders.size() > 0) {
            MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
            StreamSupport.stream(propSrcs.spliterator(), false)
                    .filter(ps -> ps instanceof EnumerablePropertySource)
                    .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                    .flatMap(Arrays::<String>stream)
                    .filter(propName -> propName.startsWith("openpos.tagconfig.tags") && propName.contains("name"))
                    .forEach(propName -> {
                        for (ITagProvider tagProvider :
                                this.tagProviders) {
                            String name = env.getProperty(propName);
                            String value = tagProvider.getTagValue(deviceModel.getDeviceId(), deviceModel.getAppId(), name, businessUnitId);
                            if (isNotBlank(value)) {
                                deviceModel.setTagValue(name, value);
                            }
                        }
                    });
        }

        devicesRepository.saveDevice(deviceModel);
    }

    @Override
    public void onApplicationEvent(DeviceConnectedEvent event) {
        try {
            updateDevice(devicesRepository.getDevice(event.getDeviceId()));
            log.info("A device just connected.  Updated the device model in the database. {}-{}", event.getDeviceId(), event.getAppId());
            if (cacheManager != null) {
                cacheManager.getCache("/context/config").clear();
                cacheManager.getCache("/context/buttons").clear();
                cacheManager.getCache("/devices/device").clear();
            }
        } catch (DeviceNotFoundException ex) {
            // ignore
        }
    }
}
