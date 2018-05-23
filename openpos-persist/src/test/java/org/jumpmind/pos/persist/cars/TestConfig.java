package org.jumpmind.pos.persist.cars;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.DatabaseScriptContainer;
import org.jumpmind.pos.persist.driver.Driver;
import org.jumpmind.pos.persist.impl.DatabaseSchema;
import org.jumpmind.pos.persist.impl.QueryTemplates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Configuration
@ComponentScan(
        basePackages = { "org.jumpmind.pos" })
@PropertySource(value = { "classpath:test.properties"})
public class TestConfig {

    protected DBSessionFactory sessionFactory;
    InputStream queryYamlStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-query.yaml");
    QueryTemplates queryTemplates = new Yaml(new Constructor(QueryTemplates.class)).load(queryYamlStream);    

    @Bean
    public DBSessionFactory sessionFactory() {
        if (sessionFactory == null) {
            Driver.register(null);  // Load openpos driver wrapper.
            sessionFactory = new DBSessionFactory();

            Map<String, String> sessionContext = new HashMap<>();
            sessionContext.put("module.tablePrefix", "tst");
            sessionContext.put("CREATE_BY", "openpos-test");
            sessionContext.put("LAST_UPDATE_BY", "openpos-test");
            
            sessionFactory.setDatabaseSchema(new DatabaseSchema());
            sessionFactory.init(
                    PersistTestUtil.testDbPlatform(), 
                    PersistTestUtil.getSessionContext(), 
                    Arrays.asList(CarEntity.class, CarStats.class, ServiceInvoice.class), 
                    queryTemplates);
            

            DBSession session = sessionFactory.createDbSession();

            updateDataModel(session);

        }

        return sessionFactory;
    }
    
    public void updateDataModel(DBSession session) {
        String fromVersion = null;

        DatabaseScriptContainer scripts = new DatabaseScriptContainer("test/sql", PersistTestUtil.testDbPlatform());

        scripts.executePreInstallScripts(fromVersion, "0.0.1");

        sessionFactory.getDatabaseSchema().createAndUpgrade();

        scripts.executePostInstallScripts(fromVersion, "0.0.1");
    }


}
