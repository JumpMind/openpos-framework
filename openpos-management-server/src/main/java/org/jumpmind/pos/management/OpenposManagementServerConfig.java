package org.jumpmind.pos.management;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="openpos.management-server")
@Data
@Slf4j
public class OpenposManagementServerConfig {

    @NotNull
    private String mainWorkDirPath;
    private ClientConnect clientConnect;
    private String devicePattern;
    @NotNull 
    private String statusUrlTemplate;
    private long statusMaxWaitMillis = 1000;
    private long statusCheckPeriodMillis = 5000;
    private long failedStartupProcessRetentionPeriodMillis = 30000;
    
    private DeviceProcessConfig defaultDeviceProcessConfig;
    private DeviceProcessConfig[] selectorDeviceProcessConfigs;

    public DeviceProcessConfig getDeviceProcessConfig(DeviceProcessInfo dpi) {
        return this.getDeviceProcessConfig(dpi.getAppId());
    }
    
    public DeviceProcessConfig getDeviceProcessConfig(String appId) {
        DeviceProcessConfig matchedConfig = null;
        if (ArrayUtils.isEmpty(selectorDeviceProcessConfigs) || StringUtils.isBlank(appId)) {
            matchedConfig = defaultDeviceProcessConfig;
        } else {
            for(DeviceProcessConfig dpCfg : this.selectorDeviceProcessConfigs) {
                if (dpCfg.getAppId().equalsIgnoreCase(appId)) {
                    matchedConfig = dpCfg;
                    break;
                }
            }
            
            if (matchedConfig == null) {
                log.warn("No Device Process config found for appId '{}', selecting defaultDeviceProcessConfig", appId);
                matchedConfig = defaultDeviceProcessConfig;
            }
        }        
        return matchedConfig;
    }
    
    @Data
    public static class ClientConnect {
        private String hostname;
        private String webServiceBaseUrlTemplate;
        private String secureWebServiceBaseUrlTemplate;
        private String webSocketBaseUrlTemplate;
        private String secureWebSocketBaseUrlTemplate;
    }

    @Data
    public static class DeviceProcessConfig {
        public static final String AUTO_PORT_ALLOCATION = "AUTO";
        public static final String PROVIDED_PORT_ALLOCATION = "PROVIDED";
        public static final String DEFAULT_PROCESS_LOG_FILENAME = "process.log";
        private String appId;
        private String initializationScript;
        private long startMaxWaitMillis = 60000;
        private long deadProcessRetentionPeriodMillis = 120000;
        private String processLogFilePath = DEFAULT_PROCESS_LOG_FILENAME;
        private boolean reuseProcessPortEnabled = false;
        private ExecutableConfig executableConfig;
        private JavaExecutableConfig javaExecutableConfig;
                
    }

    @Data
    public static class ExecutableConfig {
        private String executablePath;
        private String[] arguments = new String[0];
        
        private String shutdownExecutablePath;
        private String[] shutdownCommandArguments = new String[0];
        
        public boolean isEmpty() {
            return StringUtils.isEmpty(this.executablePath);
        }
        
        public boolean isNotEmpty() {
            return ! this.isEmpty();
        }
        
        public boolean isShutdownCommandRequired() {
            return this.isNotEmpty() && StringUtils.isNotBlank(this.shutdownExecutablePath);
        }
    }
    
    @Data
    public static class JavaExecutableConfig {
        public static final String DEFAULT_PROCESS_PORT_ARG_TEMPLATE = "-Dserver.port=%d";
        public static final String DEFAULT_JAVA_REMOTE_DEBUG_ARG_TEMPLATE = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=%d";
        
        @NotNull @Value("${java.home}")
        private String javaExecutablePath;

        private String[] classpathEntries = new String[0];
        private String[] additionalJavaArguments = new String[0];
        private String executableJarPath;
        private String mainClass;
        private String processPort = DeviceProcessConfig.AUTO_PORT_ALLOCATION;
        private String processPortArgTemplate = DEFAULT_PROCESS_PORT_ARG_TEMPLATE;
        private String[] processArguments = new String[0];
        private String javaRemoteDebugPort;
        private String javaRemoteDebugArgTemplate = DEFAULT_JAVA_REMOTE_DEBUG_ARG_TEMPLATE;
    }    
}
