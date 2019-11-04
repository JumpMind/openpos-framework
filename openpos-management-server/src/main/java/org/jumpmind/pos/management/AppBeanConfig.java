package org.jumpmind.pos.management;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppBeanConfig {

    @Autowired
    OpenposManagementServerConfig config;
    
    @Bean
    public ScriptEngine groovyScriptEngine() {
        return new ScriptEngineManager().getEngineByName("groovy"); 
    }

    @Bean
    public ImpersonalizationResponse defaultImpersonalizationResponse() {
        return new ImpersonalizationResponse(true, config.getDevicePattern());
    }
}
