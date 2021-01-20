package org.jumpmind.pos.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@ComponentScan(basePackages = { "org.jumpmind.pos" })
@SpringBootApplication
@EnableConfigurationProperties
public class TestDevicesConfig {

    @Autowired
    DevicesModule module;

    @PostConstruct
    public void init() {
    }

}