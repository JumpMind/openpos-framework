package org.jumpmind.pos.app;

import java.lang.reflect.Method;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jumpmind.pos.persist.driver.Driver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(
        basePackages = { "org.jumpmind.pos" })
@PropertySource(value = { "classpath:openpos-defaults.properties", "file:./config/openpos.properties" }, ignoreResourceNotFound = true)
public class OpenPOSServer {
    
    private static Logger log;

    public static void main(String[] args) throws Exception {
        initLogging();
        loadJumpMindDriver();
        
        boolean handled = false;
        
        if (args != null && args.length > 0 && !args[0].startsWith("-")) {
            Class<?> clazz = Class.forName(args[0]);
            Method method = clazz.getMethod("main", String[].class);
            String[] param1 = new String[0];
            method.invoke(clazz, new Object[] { param1});
            handled = true;
        }
        
        if (!handled) {            
            SpringApplication.run(OpenPOSServer.class, args);
        }
    }

    protected static void loadJumpMindDriver() {
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    protected static void initLogging() {
        String origValue = System.getProperty("log4j.defaultInitOverride");
        try {
            System.setProperty("log4j.defaultInitOverride", "true");
            URL log4jXmlURL = Thread.currentThread().getContextClassLoader().getResource("log4j.xml");
            DOMConfigurator.configure(log4jXmlURL);
            log = Logger.getLogger(OpenPOSServer.class);
            final String msg = "OpenPOS logging enabled from " + log4jXmlURL + "...";
            log.info(msg);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            if (origValue == null) {
                System.clearProperty("log4j.defaultInitOverride");
            } else {                
                System.setProperty("log4j.defaultInitOverride", origValue);
            }
        }
    }
}
