package org.jumpmind.pos.persist.cars;

import java.io.InputStream;
import java.util.*;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.DatabaseScriptContainer;
import org.jumpmind.pos.persist.driver.Driver;
import org.jumpmind.pos.persist.impl.QueryTemplates;
import org.jumpmind.pos.persist.impl.ShadowTablesConfigModel;
import org.jumpmind.pos.persist.model.*;
import org.jumpmind.pos.util.clientcontext.ClientContext;
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
            augmenterConfig.setName("options");
            augmenterConfig.setPrefix("OPTION_");

            List<AugmenterModel> augmenterModels = new ArrayList<>();
            AugmenterModel augmenterModel = new AugmenterModel();
            augmenterModel.setName("color");
            augmenterModels.add(augmenterModel);
            augmenterModel = new AugmenterModel();
            augmenterModel.setName("transmission");
            augmenterModel.setDefaultValue("standard");
            augmenterModels.add(augmenterModel);

            List<AugmenterIndexConfig> indexConfigs = new ArrayList<>();

            AugmenterIndexConfig indexConfig = new AugmenterIndexConfig();
            indexConfig.setName("idx_option_color");
            indexConfig.setColumnNames(Arrays.asList("color"));
            indexConfigs.add(indexConfig);

            indexConfig = new AugmenterIndexConfig();
            indexConfig.setName("idx_make_model_color");
            indexConfig.setColumnNames(Arrays.asList("make", "model", "color"));
            indexConfigs.add(indexConfig);
            augmenterConfig.setIndexConfigs(indexConfigs);
            augmenterConfig.setAugmenters(augmenterModels);

            AugmenterConfig classifierAugmenterConfig = new AugmenterConfig();
            classifierAugmenterConfig.setName("classifiers");
            classifierAugmenterConfig.setPrefix("CLASS_");
            List<AugmenterModel> classifierAugmenters = new ArrayList<>();
            augmenterModel = new AugmenterModel();
            augmenterModel.setName("department");
            classifierAugmenters.add(augmenterModel);
            augmenterModel = new AugmenterModel();
            augmenterModel.setName("section");
            classifierAugmenters.add(augmenterModel);
            classifierAugmenterConfig.setAugmenters(classifierAugmenters);

            augmenterConfigs.setConfigs(Arrays.asList(augmenterConfig, classifierAugmenterConfig));

            AugmenterHelper augmenterHelper = new AugmenterHelper();
            augmenterHelper.setAugmenterConfigs(augmenterConfigs);

            ClientContext clientContext = new ClientContext();

            ShadowTablesConfigModel shadowTablesConfig = new ShadowTablesConfigModel(
                    "training",
                    "tng",
                    true,
                    Arrays.asList("car_*"),
                    Arrays.asList("car_stats")
            );

            sessionFactory.init(
                    PersistTestUtil.testDbPlatform(), 
                    PersistTestUtil.getSessionContext(), 
                    Arrays.asList(CarModel.class, CarStats.class, ServiceInvoice.class, RaceCarModel.class, AugmentedCarModel.class, ScriptVersionModel.class, MultiAugmentedCarModel.class),
                    Arrays.asList(CarModelExtension.class),
                    queryTemplates,
                    DBSessionFactory.getDmlTemplates("persist-test"),
                    tagHelper,
                    augmenterHelper,
                    clientContext,
                    shadowTablesConfig);
            

            DBSession session = sessionFactory.createDbSession();

            updateDataModel(session);

        }

        return sessionFactory;
    }
    
    public void updateDataModel(DBSession session) {
        DatabaseScriptContainer scripts = new DatabaseScriptContainer(Arrays.asList("persist-test/sql"), session, "test");
        scripts.executePreInstallScripts(true);
        sessionFactory.createAndUpgrade();
        scripts.executePostInstallScripts(true);
    }


}
