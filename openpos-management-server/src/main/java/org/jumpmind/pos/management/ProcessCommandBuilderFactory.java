package org.jumpmind.pos.management;

import org.jumpmind.pos.management.OpenposManagementServerConfig.DeviceProcessConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessCommandBuilderFactory {
    @Autowired
    JavaProcessCommandBuilderImpl javaProcessCommandBuilder;
    @Autowired
    ExecutableProcessCommandBuilderImpl executableCommandBuilder;
    
    ProcessCommandBuilder make(DeviceProcessConfig config) {
        if (config.getExecutableConfig() != null && config.getExecutableConfig().isNotEmpty()) {
            return executableCommandBuilder; 
        } else {
            return javaProcessCommandBuilder;
        }
    }
}
