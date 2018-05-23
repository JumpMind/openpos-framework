package org.jumpmind.pos;

import java.lang.reflect.Method;

import org.jumpmind.pos.persist.driver.Driver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(
        basePackages = { "com.jumpmind.cst", "org.jumpmind.pos.core", "org.jumpmind.pos.app", "org.jumpmind.pos" })
@PropertySource(value = { "classpath:openpos-defaults.properties", "file:./conf/openpos.properties" }, ignoreResourceNotFound = true)
public class AppServer {

    public static void main(String[] args) throws Exception {
        loadJumpMindDriver();
        if (args == null || args.length == 0) {           
            SpringApplication.run(AppServer.class, args);
        } else if (args != null && args.length > 0) {
            Class<?> clazz = Class.forName(args[0]);
            Method method = clazz.getMethod("main", String[].class);
            String[] param1 = new String[0];
            method.invoke(clazz, new Object[] { param1});
        }
    }
    
    protected static void loadJumpMindDriver() {
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}