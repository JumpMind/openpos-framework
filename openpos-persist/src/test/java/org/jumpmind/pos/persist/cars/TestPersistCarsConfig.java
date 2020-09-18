package org.jumpmind.pos.persist.cars;

import java.io.InputStream;
import java.util.*;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.DatabaseScriptContainer;
import org.jumpmind.pos.persist.driver.Driver;
import org.jumpmind.pos.persist.impl.QueryTemplates;
import org.jumpmind.pos.persist.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@ComponentScan(
        basePackages = { "org.jumpmind.pos" })
@PropertySource(value = { "classpath:persist-test.properties"})
public class TestPersistCarsConfig {

    protected DBSessionFactory sessionFactory;
    InputStream queryYamlStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("persist-test-query.yml");
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
            
            TagConfig tagConfig = new TagConfig();
            TagModel tagModel = new TagModel();
            tagModel.setGroup("LOCATION");
            tagModel.setLevel(1);
            tagModel.setName("DEALERSHIP_NUMBER");
            tagConfig.getTags().add(tagModel);
            
            TagHelper tagHelper = new TagHelper();
            tagHelper.setTagConfig(tagConfig);

            AugmenterConfigs augmenterConfigs = new AugmenterConfigs();
            AugmenterConfig augmenterConfig = new AugmenterConfig();
            augmenterConfig.setGroup("options");
            augmenterConfig.setPrefix("OPTION_");

            List<AugmenterModel> augmenterModels = new ArrayList<>();
            AugmenterModel augmenterModel = new AugmenterModel();
            augmenterModel.setName("color");
            augmenterModel.setOrder(0);
            augmenterModels.add(augmenterModel);
            augmenterModel = new AugmenterModel();
            augmenterModel.setName("transmission");
            augmenterModel.setDefaultValue("standard");
            augmenterModel.setOrder(1);
            augmenterModels.add(augmenterModel);
            augmenterConfig.setAugmenters(augmenterModels);
            augmenterConfigs.setConfigs(Arrays.asList(augmenterConfig));

            AugmenterHelper augmenterHelper = new AugmenterHelper();
            augmenterHelper.setAugmenterConfigs(augmenterConfigs);

            sessionFactory.init(
                    PersistTestUtil.testDbPlatform(), 
                    PersistTestUtil.getSessionContext(), 
                    Arrays.asList(CarModel.class, CarStats.class, ServiceInvoice.class, RaceCarModel.class, AugmentedCarModel.class),
                    Arrays.asList(CarModelExtension.class),
                    queryTemplates,
                    DBSessionFactory.getDmlTemplates("persist-test"),
                    tagHelper,
                    augmenterHelper);
            

            DBSession session = sessionFactory.createDbSession();

            updateDataModel(session);

        }

        return sessionFactory;
    }
    
    public void updateDataModel(DBSession session) {
        String fromVersion = null;

        DatabaseScriptContainer scripts = new DatabaseScriptContainer("persist-test/sql", PersistTestUtil.testDbPlatform());

        scripts.executePreInstallScripts(fromVersion, "0.0.1", true);

        sessionFactory.createAndUpgrade();

        scripts.executePostInstallScripts(fromVersion, "0.0.1", true);
    }


}
