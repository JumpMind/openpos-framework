package org.jumpmind.pos.core.service.spring;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceScopeConfig {
    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new DeviceScopeBeanFactoryPostProcessor();
    }
}
