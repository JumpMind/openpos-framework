package org.jumpmind.pos.symds;

import org.jumpmind.symmetric.ISymmetricEngine;
import org.jumpmind.symmetric.common.ParameterConstants;
import org.jumpmind.symmetric.common.ServerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.*;

@Component
@Order(Integer.MIN_VALUE)
public class DefaultSymDSConfigurator implements ISymDSConfigurator {

    @Autowired
    Environment env;

    @Value("${openpos.installationId:'not set'}")
    String installationId;

    @Override
    public void beforeCreate(Properties properties) {
        properties.put(ParameterConstants.DATA_LOADER_IGNORE_MISSING_TABLES, "true");
        properties.put(ParameterConstants.TRIGGER_CREATE_BEFORE_INITIAL_LOAD, "false");
        properties.put(ParameterConstants.AUTO_RELOAD_ENABLED, "true");
        properties.put(ParameterConstants.AUTO_REGISTER_ENABLED, "true");
        properties.put(ParameterConstants.INITIAL_LOAD_USE_EXTRACT_JOB, "false");
        properties.put(ParameterConstants.JOB_PUSH_PERIOD_TIME_MS, "10000");
        properties.put(ParameterConstants.INITIAL_LOAD_EXTRACT_JOB_START, "false");
        properties.put(ServerConstants.HTTPS2_ENABLE, "false");
        properties.setProperty(ParameterConstants.DATA_LOADER_MAX_ROWS_BEFORE_COMMIT, "3000");
        String nodeGroupId = env.getProperty("openpos.symmetric.nodeGroupId");
        if (isNotBlank(nodeGroupId)) {
            properties.put(ParameterConstants.NODE_GROUP_ID, nodeGroupId);
            properties.put(ParameterConstants.ENGINE_NAME, nodeGroupId);
        }

        properties.put(ParameterConstants.EXTERNAL_ID, trim(installationId));

        String registrationUrl = env.getProperty("openpos.symmetric.registrationUrl");
        if (isNotBlank(registrationUrl)) {
            properties.put(ParameterConstants.REGISTRATION_URL, registrationUrl);
        }

        String syncUrl = env.getProperty("openpos.symmetric.syncUrl");
        if (isNotBlank(syncUrl)) {
            properties.put(ParameterConstants.SYNC_URL, syncUrl);
        }
    }

    @Override
    public void beforeStart(ISymmetricEngine engine) {

    }

    @Override
    public String getWebContext() {
        return "/symds/*";
    }
}
