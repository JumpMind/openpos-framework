package org.jumpmind.pos.management;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@EnableScheduling
@Slf4j
@ComponentScan(basePackages = {"org.jumpmind.pos"})
public class OpenposManagementServer {
    public static String STARTUP_INIT_CLASS_PROPERTY = "openpos.managementServer.startup.initClass";
    

    public static void main(String[] args) throws Exception {
        System.out.println("OpenposManagementServer working dir: " + System.getProperty("user.dir"));
        invokeOptionalStartupClass();
        SpringApplication application = new SpringApplication(OpenposManagementServer.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }
    
    protected static void invokeOptionalStartupClass() {
        String initClass = System.getProperty(STARTUP_INIT_CLASS_PROPERTY);
        if (initClass != null && ! initClass.trim().isEmpty()) {
            try {
                initClass = initClass.trim();
                log.info("Invoking main method of provided initClass '{}'...", initClass);
                Class<?> cls = Class.forName(initClass);
                Method meth = cls.getMethod("main", String[].class);
                String[] params = null;
                meth.invoke(null, (Object) params);
                log.info("main method of initClass '{}' executed successfully.", initClass);
            } catch (Exception ex) {
                String msg = String.format("Failed to execute main method on startup initClass: '%s'", initClass);
                log.error(msg, ex);
                throw new OpenposManagementException(msg, ex);
            }
        }
    }

    @Bean
    CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(caffeineCacheBuilder());
        return manager;
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES);
    }
}
