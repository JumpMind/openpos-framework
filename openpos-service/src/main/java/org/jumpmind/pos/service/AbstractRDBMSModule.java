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
import static org.jumpmind.pos.service.util.ClassUtils.getClassesForPackageAndAnnotation;
import static org.apache.commons.lang3.StringUtils.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.JdbcDatabasePlatformFactory;
import org.jumpmind.db.sql.SqlException;
import org.jumpmind.db.sql.SqlTemplateSettings;
import org.jumpmind.db.util.BasicDataSourceFactory;
import org.jumpmind.db.util.ConfigDatabaseUpgrader;
import org.jumpmind.exception.IoException;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.DatabaseScriptContainer;
import org.jumpmind.pos.persist.PersistException;
import org.jumpmind.pos.persist.TableDef;
import org.jumpmind.pos.persist.driver.Driver;
import org.jumpmind.pos.persist.model.TagHelper;
import org.jumpmind.pos.service.model.ModuleModel;
import org.jumpmind.properties.TypedProperties;
import org.jumpmind.security.ISecurityService;
import org.jumpmind.security.SecurityServiceFactory;
import org.jumpmind.symmetric.io.data.DbExport;
import org.jumpmind.symmetric.io.data.DbExport.Compatible;
import org.jumpmind.symmetric.io.data.DbExport.Format;
import org.jumpmind.util.AbstractVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@DependsOn({"tagConfig"})
abstract public class AbstractRDBMSModule extends AbstractServiceFactory implements IModule, IRDBMSModule {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected Environment env;

    @Value("${openpos.installationId:undefined}")
    protected String installationId;

    @Value("${openpos.businessunitId:undefined}")
    protected String businessUnitId;

    @Autowired
    protected TagHelper tagHelper;

    @Autowired
    protected ApplicationContext applicationContext;

    protected IDatabasePlatform databasePlatform;

    @Value("${openpos.general.datasourceBeanName:#{null}}")
    protected String dataSourceBeanName;

    @Value("${openpos.general.sqlScriptProfile:test}")
    protected String sqlScriptProfile;

    protected DataSource dataSource;

    protected ISecurityService securityService;

    protected PlatformTransactionManager txManager;

    protected DBSessionFactory sessionFactory;

    static Server h2Server;

    @Override
    public String getVersion() {
        String version = new AbstractVersion() {
            @Override
            protected String getArtifactName() {
                return AbstractRDBMSModule.this.getArtifactName();
            }
        }.version();
        if (version.equals("development")) {
            version = findDevelopmentVersion();
        }
        return version;
    }

    private String findDevelopmentVersion() {
        return "1000.0.0";
    }

    abstract protected String getArtifactName();

    protected void setupH2Server() {
        if ("true".equals(env.getProperty("db.h2.startServer"))) {
            String configDbUrl = getDbProperties(DB_POOL_URL, "jdbc:h2:mem:config");
            if (h2Server == null && configDbUrl.contains("h2:tcp")) {
                try {
                    h2Server = Server.createTcpServer("-tcpPort", env.getProperty("db.h2.port", "1973"));
                    ((Server) h2Server).start();
                } catch (SQLException e) {
                    throw new SqlException(e);
                }
            }
        }
    }


    @Override
    public PlatformTransactionManager getPlatformTransactionManager() {
        if (txManager == null) {
            this.txManager = new DataSourceTransactionManager(getDataSource());
        }
        return txManager;
    }

    protected IDatabasePlatform databasePlatform() {
        if (databasePlatform == null) {
            databasePlatform = JdbcDatabasePlatformFactory.createNewPlatformInstance(getDataSource(), new SqlTemplateSettings(), false, false);
        }
        return databasePlatform;
    }

    @Override
    public IDatabasePlatform getDatabasePlatform() {
        return databasePlatform();
    }

    protected ISecurityService securityService() {
        if (this.securityService == null) {
            this.securityService = SecurityServiceFactory.create();
        }
        return this.securityService;
    }

    protected String getDbProperties(String propertyName, String defaultValue) {
        final String CANNOT_BE_THIS = "CANNOT BE THIS";
        String value = env.getProperty(String.format("%s.%s", getName(), propertyName), CANNOT_BE_THIS);
        if (CANNOT_BE_THIS.equals(value)) {
            value = env.getProperty(propertyName, defaultValue);
        }
        return value;
    }

    @Override
    public String getDriver() {
        return getDbProperties(DB_POOL_DRIVER, "org.h2.Driver");
    }

    @Override
    public String getURL() {
        return getDbProperties(DB_POOL_URL, "jdbc:openpos:h2:mem:" + getName());
    }

    @Override
    public DataSource getDataSource() {
        if (dataSource == null) {
            setupH2Server();
            if (this.dataSourceBeanName != null) {
                try {
                    dataSource = this.applicationContext.getBean(this.dataSourceBeanName, DataSource.class);
                    log.info("Using dataSource bean '{}' for {} module dataSource", this.dataSourceBeanName, getName());
                } catch (Exception ex) {
                    log.warn("Failed to load dataSource with name '{}', will load default dataSource instead. Reason: {}",
                            this.dataSourceBeanName, ex.getMessage());
                }
            }

            if (dataSource == null) {
                Driver.class.getName(); // Load openpos driver wrapper.
                TypedProperties properties = new TypedProperties();
                properties.put(DB_POOL_DRIVER, getDriver());
                properties.put(DB_POOL_URL, getURL());
                properties.put(DB_POOL_USER, getDbProperties(DB_POOL_USER, null));
                properties.put(DB_POOL_PASSWORD, getDbProperties(DB_POOL_PASSWORD, null));
                properties.put(DB_POOL_INITIAL_SIZE, getDbProperties(DB_POOL_INITIAL_SIZE, "5"));
                properties.put(DB_POOL_MAX_ACTIVE, getDbProperties(DB_POOL_MAX_ACTIVE, "5"));
                properties.put(DB_POOL_MAX_IDLE, getDbProperties(DB_POOL_MAX_IDLE, "5"));
                properties.put(DB_POOL_MIN_IDLE, getDbProperties(DB_POOL_MIN_IDLE, "5"));
                properties.put(DB_POOL_MAX_WAIT, getDbProperties(DB_POOL_MAX_WAIT, "30000"));
                properties.put(DB_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS, getDbProperties(DB_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS, "120000"));
                properties.put(DB_POOL_VALIDATION_QUERY, getDbProperties(DB_POOL_VALIDATION_QUERY, null));
                properties.put(DB_POOL_TEST_ON_BORROW, getDbProperties(DB_POOL_TEST_ON_BORROW, "false"));
                properties.put(DB_POOL_TEST_ON_RETURN, getDbProperties(DB_POOL_TEST_ON_RETURN, "false"));
                properties.put(DB_POOL_TEST_WHILE_IDLE, getDbProperties(DB_POOL_TEST_WHILE_IDLE, "true"));
                properties.put(DB_POOL_INIT_SQL, getDbProperties(DB_POOL_INIT_SQL, null));
                properties.put(DB_POOL_CONNECTION_PROPERTIES, getDbProperties(DB_POOL_CONNECTION_PROPERTIES, null));
                log.info(String.format(
                        "About to initialize the '%s' module datasource using the following driver:"
                                + " '%s' and the following url: '%s' and the following user: '%s'",
                        getName(), properties.get(DB_POOL_DRIVER), properties.get(DB_POOL_URL), properties.get(DB_POOL_USER)));

                dataSource = BasicDataSourceFactory.create(properties, securityService());
            }
        }
        return dataSource;
    }

    protected DBSessionFactory sessionFactory() {
        if (sessionFactory == null) {
            Driver.register(null); // Load openpos driver wrapper.
            // Driver.class.getName();
            sessionFactory = new DBSessionFactory();

            String packageName = this.getClass().getPackage().getName();

            List<Class<?>> tableClasses = getClassesForPackageAndAnnotation(packageName, TableDef.class);

            TypedProperties sessionContext = new TypedProperties();

            sessionContext.put("module.tablePrefix", getTablePrefix());
            sessionContext.put("CREATE_BY", "openpos-" + getName());
            sessionContext.put("LAST_UPDATE_BY", "openpos-" + getName());
            sessionContext.put(DBSession.JDBC_FETCH_SIZE, env.getProperty(DBSession.JDBC_FETCH_SIZE));
            sessionContext.put(DBSession.JDBC_QUERY_TIMEOUT, env.getProperty(DBSession.JDBC_QUERY_TIMEOUT));

            sessionFactory.init(getDatabasePlatform(), sessionContext, tableClasses, tagHelper);

        }

        return sessionFactory;
    }

    @Override
    public void initialize() {
        updateDataModel(getDBSession());
    }

    public void exportData(String format, String dir, boolean includeModuleTables) {
        try (OutputStream os = new BufferedOutputStream(
                new FileOutputStream(new File(dir, String.format("%s_post_01_%s.sql", getVersion(), getName().toLowerCase()))))) {
            List<Table> tables = this.sessionFactory.getTables(includeModuleTables ? new Class<?>[0] : new Class[]{ModuleModel.class});
            DbExport dbExport = new DbExport(this.databasePlatform);
            dbExport.setCompatible(Compatible.H2);
            dbExport.setUseQuotedIdentifiers(false);
            dbExport.setNoData(false);
            dbExport.setFormat(Format.valueOf(format));
            dbExport.setNoCreateInfo(true);
            dbExport.exportTables(os, tables.toArray(new Table[tables.size()]));
        } catch (IOException e) {
            throw new IoException(e);
        }
    }

    public void updateDataModel(DBSession session) {
        String fromVersion = null;

        try {
            ModuleModel info = session.findByNaturalId(ModuleModel.class, installationId);
            if (info != null) {
                fromVersion = info.getCurrentVersion();
            }
        } catch (PersistException e) {
            log.info("The module table is not available");
        }

        log.info("The previous version of {} was {} and the current version is {}", getName(), fromVersion, getVersion());
        log.info("The sqlScriptProfile is: {}", sqlScriptProfile);
        DatabaseScriptContainer scripts = new DatabaseScriptContainer(String.format("%s/sql/%s", getName(), sqlScriptProfile),
                getDatabasePlatform());

        IDBSchemaListener schemaListener = getDbSchemaListener();

        scripts.executePreInstallScripts(fromVersion, getVersion());
        schemaListener.beforeSchemaCreate(sessionFactory);
        sessionFactory.createAndUpgrade();
        upgradeDbFromXml();
        schemaListener.afterSchemaCreate(sessionFactory);
        scripts.executePostInstallScripts(fromVersion, getVersion());

        session.save(new ModuleModel(installationId, getVersion()));
    }

    protected void upgradeDbFromXml() {
        String path = "/" + getName() + "-schema.xml";
        URL resource = getClass().getResource(path);
        if (resource != null) {
            ConfigDatabaseUpgrader databaseUpgrade = new ConfigDatabaseUpgrader(path, getDatabasePlatform(), true, "");
            databaseUpgrade.upgrade();
        }
    }

    @Override
    public DBSession getDBSession() {
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
