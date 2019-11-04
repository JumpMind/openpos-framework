package org.jumpmind.pos.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jumpmind.pos.management.OpenposManagementServerConfig.ExecutableConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Builds a list of arguments required to run a command to start or kill a process 
 * for a Device.
 */
@Slf4j
@Component
public class ExecutableProcessCommandBuilderImpl implements ProcessCommandBuilder {
    @Autowired
    OpenposManagementServerConfig config;

    @Override
    public List<String> constructProcessCommandParts(DeviceProcessInfo pi) {
        ExecutableConfig exeProcessCfg = config.getDeviceProcessConfig(pi).getExecutableConfig();
        List<String> commandLineArgs = new ArrayList<>();
        commandLineArgs.add(constructProcessExecutablePath(exeProcessCfg, pi));
        
        String[] processArgs = constructProcessArguments(exeProcessCfg, pi);
        if (ArrayUtils.isNotEmpty(processArgs)) {
            commandLineArgs.addAll(Arrays.asList(processArgs));
        }
        
        return commandLineArgs;
    }

    @Override
    public List<String> constructKillCommandParts(DeviceProcessInfo pi) {
        ExecutableConfig exeProcessCfg = config.getDeviceProcessConfig(pi).getExecutableConfig();
        List<String> commandLineArgs = new ArrayList<>();

        if (exeProcessCfg.isShutdownCommandRequired()) {
            commandLineArgs.add(exeProcessCfg.getShutdownExecutablePath().replaceAll("\\$deviceId", pi.getDeviceId()));
            
            if (ArrayUtils.isNotEmpty(exeProcessCfg.getShutdownCommandArguments())) {
                commandLineArgs.addAll(Arrays.asList(exeProcessCfg.getShutdownCommandArguments()));
            }
        }
        
        return commandLineArgs;
    }
    
    protected String constructProcessExecutablePath(ExecutableConfig executableCfg, DeviceProcessInfo pi) {
        return executableCfg.getExecutablePath().replaceAll("\\$deviceId", pi.getDeviceId());
    }
    
    protected String[] constructProcessArguments(ExecutableConfig executableCfg, DeviceProcessInfo pi) {
        return executableCfg.getArguments();
    }

    
}
