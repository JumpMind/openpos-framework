package org.jumpmind.pos.service;

import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_CONNECTION_PROPERTIES;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_DRIVER;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_INITIAL_SIZE;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_INIT_SQL;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_MAX_ACTIVE;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_MAX_IDLE;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_MAX_WAIT;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_MIN_IDLE;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_PASSWORD;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_TEST_ON_BORROW;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_TEST_ON_RETURN;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_TEST_WHILE_IDLE;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_URL;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_USER;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.DB_POOL_VALIDATION_QUERY;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.h2.tools.Server;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.JdbcDatabasePlatformFactory;
import org.jumpmind.db.sql.SqlTemplateSettings;
import org.jumpmind.db.util.BasicDataSourceFactory;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.DatabaseScriptContainer;
import org.jumpmind.pos.persist.PersistException;
import org.jumpmind.pos.persist.Table;
import org.jumpmind.pos.persist.driver.Driver;
import org.jumpmind.pos.service.model.ModuleInfo;
import org.jumpmind.properties.TypedProperties;
import org.jumpmind.security.ISecurityService;
import org.jumpmind.security.SecurityServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
abstract public class AbstractModule implements Module {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected Environment env;

    @Value("${installation.id}")
    protected String installationId;

    /**
     * This is to make the modules dependent on the h2 server being created
     */
    @Autowired(required=false)
    Server h2Server;

    protected IDatabasePlatform databasePlatform;

    protected BasicDataSource dataSource;

    protected ISecurityService securityService;

    protected PlatformTransactionManager txManager;

    protected DBSessionFactory sessionFactory;

    protected List<Class<?>> getClassesForPackageAndAnnotation(String packageName, Class<? extends Annotation> annotation) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(ModuleInfo.class);
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
        for (BeanDefinition bd : scanner.findCandidateComponents(packageName)) {
            try {
                final Class<?> clazz = Class.forName(bd.getBeanClassName());
                classes.add(clazz);
            } catch (ClassNotFoundException ex) {
                logger.error(ex.getMessage());
            }
        }
        return classes;
    }

    protected PlatformTransactionManager txManager() {
        if (txManager == null) {
            this.txManager = new DataSourceTransactionManager(dataSource());
        }
        return txManager;
    }

    protected IDatabasePlatform databasePlatform() {
        if (databasePlatform == null) {
            databasePlatform = JdbcDatabasePlatformFactory.createNewPlatformInstance(dataSource(), new SqlTemplateSettings(), false, false);
        }
        return databasePlatform;
    }

    protected ISecurityService securityService() {
        if (this.securityService == null) {
            this.securityService = SecurityServiceFactory.create();
        }
        return this.securityService;
    }
    
    public String getDriver() {
    	return env.getProperty(DB_POOL_DRIVER, "org.h2.Driver");
    }
    
    public String getURL() {
    	return env.getProperty(DB_POOL_URL, "jdbc:openpos:h2:mem:" + getName());
    }

    protected BasicDataSource dataSource() {
        if (dataSource == null) {
            Driver.class.getName(); // Load openpos driver wrapper.
            TypedProperties properties = new TypedProperties();
            properties.put(DB_POOL_DRIVER, getDriver());
            properties.put(DB_POOL_URL, getURL());
            properties.put(DB_POOL_USER, env.getProperty(DB_POOL_USER));
            properties.put(DB_POOL_PASSWORD, env.getProperty(DB_POOL_PASSWORD));
            properties.put(DB_POOL_INITIAL_SIZE, env.getProperty(DB_POOL_INITIAL_SIZE, "20"));
            properties.put(DB_POOL_MAX_ACTIVE, env.getProperty(DB_POOL_MAX_ACTIVE, "20"));
            properties.put(DB_POOL_MAX_IDLE, env.getProperty(DB_POOL_MAX_IDLE, "20"));
            properties.put(DB_POOL_MIN_IDLE, env.getProperty(DB_POOL_MIN_IDLE, "20"));
            properties.put(DB_POOL_MAX_WAIT, env.getProperty(DB_POOL_MAX_WAIT, "30000"));
            properties.put(DB_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS, env.getProperty(DB_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS, "120000"));
            properties.put(DB_POOL_VALIDATION_QUERY, env.getProperty(DB_POOL_VALIDATION_QUERY));
            properties.put(DB_POOL_TEST_ON_BORROW, env.getProperty(DB_POOL_TEST_ON_BORROW, "false"));
            properties.put(DB_POOL_TEST_ON_RETURN, env.getProperty(DB_POOL_TEST_ON_RETURN, "false"));
            properties.put(DB_POOL_TEST_WHILE_IDLE, env.getProperty(DB_POOL_TEST_WHILE_IDLE, "true"));
            properties.put(DB_POOL_INIT_SQL, env.getProperty(DB_POOL_INIT_SQL));
            properties.put(DB_POOL_CONNECTION_PROPERTIES, env.getProperty(DB_POOL_CONNECTION_PROPERTIES));
            logger.info(String.format(
                    "About to initialize the 'User' module datasource using the following driver:"
                            + " '%s' and the following url: '%s' and the following user: '%s'",
                    properties.get(DB_POOL_DRIVER), properties.get(DB_POOL_URL), properties.get(DB_POOL_USER)));

            dataSource = BasicDataSourceFactory.create(properties, securityService());
        }
        return dataSource;
    }

    protected DBSessionFactory sessionFactory() {
        if (sessionFactory == null) {
            Driver.register(null);  // Load openpos driver wrapper.
            //Driver.class.getName();
            sessionFactory = new DBSessionFactory();

            String packageName = this.getClass().getPackage().getName();

            List<Class<?>> tableClasses = getClassesForPackageAndAnnotation(packageName, Table.class);

            Map<String, String> sessionContext = new HashMap<>();

            sessionContext.put("module.tablePrefix", getTablePrefix());
            sessionContext.put("CREATE_BY", "openpos-" + getName());
            sessionContext.put("LAST_UPDATE_BY", "openpos-" + getName());

            sessionFactory.init(databasePlatform(), sessionContext, tableClasses);

            DBSession session = sessionFactory.createDbSession();

            updateDataModel(session);

        }

        return sessionFactory;
    }

    public void updateDataModel(DBSession session) {
        String fromVersion = null;

        try {
            ModuleInfo info = session.findByNaturalId(ModuleInfo.class, installationId);
            if (info != null) {
                fromVersion = info.getCurrentVersion();
            }
        } catch (PersistException e) {
            logger.info("The module table is not available");
        }

        logger.info("The previous version of {} was {} and the current version is {}", getName(), fromVersion, getVersion());

        DatabaseScriptContainer scripts = new DatabaseScriptContainer(getName() + "/sql", databasePlatform());
        
        IDBSchemaListener schemaListener = getDbSchemaListener();
        
        scripts.executePreInstallScripts(fromVersion, getVersion());
        schemaListener.beforeSchemaCreate(sessionFactory);
        sessionFactory.getDatabaseSchema().createAndUpgrade();
        schemaListener.afterSchemaCreate(sessionFactory);
        scripts.executePostInstallScripts(fromVersion, getVersion());

        session.save(new ModuleInfo(installationId, getVersion()));
    }

    protected DBSession session() {
        return sessionFactory().createDbSession();
    }
    
    protected IDBSchemaListener getDbSchemaListener() {
        return new IDBSchemaListener() {
            @Override
            public void beforeSchemaCreate(DBSessionFactory sessionFactory) {
            }
            @Override
            public void afterSchemaCreate(DBSessionFactory sessionFactory) {
            }
        };
    }
}
