package org.jumpmind.pos.management;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.util.NetworkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for setting up or accessing various aspects of the Openpos
 * Management Server execution environment.
 */
@Service
@Slf4j
public class ProcessManagerEnvironmentService {
    
    @Autowired
    OpenposManagementServerConfig config;

    /**
     * If the directory identified by the {@link OpenposManagementServerConfig#mainWorkDirPath}
     * property does not exist, it will be created.
     */
    public void ensureMainWorkDirExists() {
        File work = new File(config.getMainWorkDirPath());
        if (! work.exists()) {
            if (work.mkdirs()) {
                log.info("Openpos Management Service created working dir at '{}'", work.getPath());
            } else {
                throw new OpenposManagementException(String.format("Failed to main working directory at '%s'", work.getPath())) ;
            }
        } else {
            log.trace("Openpos Management Service working dir, '{}', already exists, no need to create", work.getPath());
        }
    }

    /**
     * Given a port number configuration parameter value from {@link OpenposManagementServerConfig}, 
     * an available port number will be returned.
     * <br/>
     * Possible {@code portParameterValue}s are as follows:<br/>
     * <ul>
     * <li><strong>"AUTO"</strong>
     * <ul><li>Indicates that a port number should be automatically and randomly 
     *         allocated between the values of 1024 and 65535</li></ul>
     * </li>
     * <li><strong>range-1,range-2,...,range-n</strong>
     * <ul><li>A comma separated list of integer port number values OR 
     * ranges that will restrict the pool of port numbers that will be attempted to
     * be assigned to the process.</li></ul>
     * </li>
     * <li><strong>"PROVIDED"</strong>
     * <ul><li>The port number will be provided by the initializationScript
     * and supplied through an output binding variable named 'processPort'. 
     * If the processPort variable is found in the binding, its value
     * will be used in the statusUrlTemplate and other url template values 
     * such as webServiceBaseUrlTemplate, etc.</li></ul>
     * </li>
     * </ul>
     * @param portParameterName The name of the configuration parameter to be used
     * for log messages.
     * @param portParameterValue The value of the port parameter.
     */
    public Integer allocatePort(String portParameterName, String portParameterValue) {
        if (StringUtils.isEmpty(portParameterValue)) {
            return null; 
        }

        if (OpenposManagementServerConfig.DeviceProcessConfig.PROVIDED_PORT_ALLOCATION.equalsIgnoreCase(portParameterValue)) {
            log.debug("A port number should have already been provided during initialization script execution");
            return null;
        }
        
        Integer allocatedPort = null;
        if (portParameterValue.contains("-") || portParameterValue.contains(",")) {
            // We have a range or multiple ports/ranges
            if (portParameterValue.contains(",")) {
                // Iterate over the ports until a free one is found
                String[] valuesOrRanges = portParameterValue.split(",");
                for(String valueOrRange : valuesOrRanges) {
                    allocatedPort = findAvailablePort(valueOrRange.trim(), portParameterName);
                    if (allocatedPort != null) {
                        break;
                    }
                }
            } else {
                allocatedPort = findAvailablePort(portParameterValue, portParameterName);
            }
        } else if (portParameterValue.equalsIgnoreCase(OpenposManagementServerConfig.DeviceProcessConfig.AUTO_PORT_ALLOCATION)) {
            allocatedPort = SocketUtils.findAvailableTcpPort();
        } else {
            try {
                allocatedPort = Integer.parseInt(portParameterValue);
            } catch (Exception ex) {
                throw new DeviceProcessConfigException(
                        String.format("Cannot convert %s value of '%s' to an integer. Reason: %s", 
                                portParameterName, portParameterValue, ex.getMessage())
                        );
            }

            // Now check if it the port is in use
            if (! NetworkUtils.isTcpPortAvailable(allocatedPort)) {
                throw new DeviceProcessConfigException(
                        String.format("Port %d specified by the %s parameter is already in use.", 
                                allocatedPort, portParameterName));
            }
        }

        if (allocatedPort == null) {
            throw new DeviceProcessConfigException(
                    String.format("Failed to find an available port in the values/ranges "
                            + "specified by the %s parameter: [%s].", portParameterName, portParameterValue));
        }
        return allocatedPort;
        
    }

    protected Integer findAvailablePort(String valueOrRange, String portParameterName) {
        if (null == valueOrRange) {
            log.warn("Given port valueOrRange is null");
            return null;
        }
        
        Integer availablePort = null;
        if (valueOrRange.contains("-")) {
            String[] split = valueOrRange.split("-");
            if (split != null && split.length > 1) {
                try {
                    Integer minPort = Integer.parseInt(split[0].trim());
                    Integer maxPort = Integer.parseInt(split[1].trim());
                    availablePort = SocketUtils.findAvailableTcpPort(minPort, maxPort);
                } catch (Exception ex) {
                    log.warn("Could not find a port in the range of '{}' from the {} parameter. Reason: {}", valueOrRange, portParameterName, ex.getMessage());
                }
            } else {
                log.warn("Could not convert port range of '{}' from the {} parameter to a range.", valueOrRange, portParameterName);
            }
        } else {
            Integer portToCheck = null;
            try { 
                portToCheck = Integer.parseInt(valueOrRange); 
                if (NetworkUtils.isTcpPortAvailable(portToCheck)) {
                    availablePort = portToCheck;
                }
            } catch (Exception ex) {
                log.warn("Could not convert port value of '{}' from the {} parameter to an integer", valueOrRange, portParameterName);
            }
        }
        
        return availablePort;
        
    }
    
    
}
