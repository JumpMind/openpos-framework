package org.jumpmind.test.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { SolrAutoConfiguration.class })
@ComponentScan(basePackages = { "org.jumpmind.pos", "org.jumpmind.test" })
@PropertySource( value = { "file:c:/store.properties", "classpath:store.properties"}, ignoreResourceNotFound = true)
public class TestApp {
    public static void main(String[] args) throws Exception {

        System.out.println("The classpath is: " + System.getProperty("java.class.path"));


        loadCookieManager();

        boolean handled = false;

        if (args != null && args.length > 0 && !args[0].startsWith("-")) {
            Class<?> clazz = Class.forName(args[0]);
            Method method = clazz.getMethod("main", String[].class);
            String[] param1 = new String[0];
            method.invoke(clazz, new Object[] { param1});
            handled = true;
        }

        if (!handled) {
            SpringApplication.run(TestApp.class, args);
        }
    }


    /**
     * AWS ELB ALB - needs to handle cookies for sticky sessions to work
     */
    protected static void loadCookieManager() {
        CookieHandler.setDefault(new CookieManager());
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }


}
