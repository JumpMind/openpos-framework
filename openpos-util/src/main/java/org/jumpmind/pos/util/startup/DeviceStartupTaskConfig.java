package org.jumpmind.pos.util.startup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "openpos.startup.device")
public class DeviceStartupTaskConfig {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ApplicationContext applicationContext;

    private List<DeviceStartupTaskDefinition> tasks;

    public void processDeviceStartupTasks(String deviceId, String appId) {
        // Get all possible IDeviceStartupTasks from Spring components
        Map<String, IDeviceStartupTask> deviceStartupTasks = applicationContext.getBeansOfType(IDeviceStartupTask.class);
        if (tasks != null && deviceStartupTasks != null) {
            for (DeviceStartupTaskDefinition def : tasks) {
                // Process configured startup tasks that match the current appId
                if (def.getAppId().equals(appId)) {
                    IDeviceStartupTask task = deviceStartupTasks.get(def.getName());
                    if (task != null) {
                        task.onDeviceStartup(deviceId, appId);
                    } else {
                        logger.warn("Could not find a IDeviceStartupTask with the name {}", def.getName());
                    }
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceStartupTaskDefinition {
        private String appId;
        private String name;
    }
}
