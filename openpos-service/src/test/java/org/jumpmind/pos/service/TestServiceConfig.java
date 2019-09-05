package org.jumpmind.pos.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan(
        basePackages = {  "org.jumpmind.pos.service" })
public class TestServiceConfig {

    @Bean
    ITest test() {
        return new Proxy();
    }
    

    @RestController("test")
    @RequestMapping("/this/is/a/test")
    interface ITest {
        public void test();
    }
    
    class Proxy implements ITest {
        @Override
        public void test() {
        }
    }
    
}
