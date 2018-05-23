package org.jumpmind.pos.app.config;

import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_URL;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.h2.server.web.WebServlet;
import org.h2.tools.Server;
import org.jumpmind.db.sql.SqlException;
import org.jumpmind.properties.TypedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class AppConfig {

    protected static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    Environment env;

    TypedProperties environmentProperties;

    Server h2Server;

    @Bean
    @Scope(value = "singleton")
    Server h2Server() {
        String configDbUrl = env.getProperty(DB_POOL_URL, "jdbc:h2:mem:config");
        if (h2Server == null && configDbUrl.contains("h2:tcp")) {
            try {
                h2Server = Server.createTcpServer("-tcpPort", env.getProperty("h2.port", "1973"));
                h2Server.start();
            } catch (SQLException e) {
                throw new SqlException(e);
            }
        }
        return h2Server;
    }

    @Bean
    public ServletRegistrationBean sqlServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new WebServlet(), "/sql/*");
        bean.addInitParameter("webAllowOthers", "false");
        bean.setLoadOnStartup(1);
        return bean;
    }
    
    @Bean
    @Scope(value = "singleton")
    TypedProperties environmentProperties() {
        if (environmentProperties == null) {
            environmentProperties = new TypedProperties();
            MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
            StreamSupport.stream(propSrcs.spliterator(), false).filter(ps -> ps instanceof EnumerablePropertySource)
                    .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames()).flatMap(Arrays::<String> stream)
                    .forEach(propName -> environmentProperties.setProperty(propName, env.getProperty(propName)));
        }
        return environmentProperties;
    }

}
