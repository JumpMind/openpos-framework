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
public class DelegateTestContextConfig {

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
            augmenterModel.setOrder(0);
            augmenterModels.add(augmenterModel);
            augmenterModel = new AugmenterModel();
            augmenterModel.setName("transmission");
            augmenterModel.setOrder(1);
            augmenterModel.setDefaultValue("standard");
            augmenterModels.add(augmenterModel);
            augmenterConfig.setAugmenters(augmenterModels);
            augmenterConfigs.setConfigs(Arrays.asList(augmenterConfig));

            AugmenterHelper augmenterHelper = new AugmenterHelper();
            augmenterHelper.setAugmenterConfigs(augmenterConfigs);


            sessionFactory.init(
                    PersistTestUtil.testDbPlatform(),
                    PersistTestUtil.getSessionContext(),
                    Arrays.asList(CarExtendedWarrantyServiceModel.class),
                    null,
                    queryTemplates,
                    DBSessionFactory.getDmlTemplates("persist-test"), tagHelper, augmenterHelper);

            DBSession session = sessionFactory.createDbSession();

        }

        return sessionFactory;
    }
}
