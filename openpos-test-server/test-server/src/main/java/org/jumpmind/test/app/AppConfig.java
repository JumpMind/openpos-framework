package org.jumpmind.test.app;

import org.jumpmind.pos.core.screeninterceptor.IMessagePropertyStrategy;
import org.jumpmind.pos.core.screeninterceptor.KeyMappingScreenPropertyStrategy;
import org.jumpmind.pos.core.ui.UIMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {


    @Bean
    @Scope("prototype")
    public IMessagePropertyStrategy<UIMessage> keyMappingScreenPropertyStrategy() {
        return new KeyMappingScreenPropertyStrategy();
    }
}
