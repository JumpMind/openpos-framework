package org.jumpmind.pos.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.Server;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.JdbcDatabasePlatformFactory;
import org.jumpmind.db.sql.SqlException;
import org.jumpmind.db.sql.SqlTemplateSettings;
import org.jumpmind.db.util.BasicDataSourceFactory;
import org.jumpmind.db.util.ConfigDatabaseUpgrader;
import org.jumpmind.exception.IoException;
import org.jumpmind.pos.persist.*;
import org.jumpmind.pos.persist.driver.Driver;
import org.jumpmind.pos.persist.impl.ShadowTablesConfigModel;
import org.jumpmind.pos.persist.model.AugmenterHelper;
import org.jumpmind.pos.persist.model.TagHelper;
import org.jumpmind.pos.service.model.ModuleModel;
import org.jumpmind.pos.util.clientcontext.ClientContext;
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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;
import static org.jumpmind.db.util.BasicDataSourcePropertyConstants.*;
import static org.jumpmind.pos.service.util.ClassUtils.getClassesForPackageAndAnnotation;

@Configuration
@EnableTransactionManagement
@DependsOn({"tagConfig", "augmenterConfigs"})
@ConfigurationProperties(prefix = "openpos.module")
abstract public class AbstractRDBMSModule extends AbstractServiceFactory implements IModule, IRDBMSModule {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final static String SYS_PROP_DISABLE_JUMPMIND_DRIVER = "jumpmind.commerce.disable.jdbc.driver";

    @Autowired
    protected Environment env;

    @Value("${openpos.installationId:undefined}")
    protected String installationId;

    @Value("${openpos.businessunitId:undefined}")
    protected String businessUnitId;

    @Value("${openpos.general.rebuildDatabase.enabled:true}")
    protected boolean rebuildDatabaseEnabled;

    @Autowired
    protected TagHelper tagHelper;

    @Autowired
    protected AugmenterHelper augmenterHelper;

    @Autowired
    protected ApplicationContext applicationContext;

    protected IDatabasePlatform databasePlatform;

    @Value("${openpos.general.datasourceBeanName:#{null}}")
    protected String dataSourceBeanName;

    @Deprecated
    @Value("${openpos.general.sqlScriptProfile:not set}")
    protected String sqlScriptProfile;

    @Value("${openpos.general.dataModelExtensionPackages:#{null}}")
    protected String additionalPackages;

    @Value("${openpos.general.failStartupOnModuleLoadFailure:false}")
    boolean failStartupOnModuleLoadFailure;

    @Autowired(required = false)
    protected DataSource dataSource;

    @Autowired
    protected ClientContext clientContext;

    protected ISecurityService securityService;

    protected PlatformTransactionManager txManager;

    protected DBSessionFactory sessionFactory;

    @Getter
    @Setter
    private ModuleLoaderConfig loaderConfig;

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
        File dir = new File("../");
        File[] files = dir.listFiles();
        for (File file: files) {
            if (file.isDirectory() && file.getName().endsWith("-assemble")) {
                File gradleProperties = new File(file, "gradle.properties");
                if (gradleProperties.exists()) {
                    TypedProperties props = new TypedProperties(gradleProperties);
                    String version = props.get("version");
                    if (version.endsWith("-SNAPSHOT")) {
                        return version.substring(0, version.length()-"-SNAPSHOT".length());
                    }
                }
            }
        }
        return "1000.0.0";
    }

    abstract protected String getArtifactName();

    protected void setupH2Server() {
        if ("true".equals(env.getProperty("db.h2.startServer"))) {
            String configDbUrl = getDbProperties(DB_POOL_URL, "jdbc:h2:mem:config");
            if (h2Server == null && configDbUrl.contains("h2:tcp")) {
                try {
                    h2Server = Server.createTcpServer("-tcpPort", env.getProperty("db.h2.port", "1973"), "-ifNotExists");
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
        String driverClassName = getDbProperties(DB_POOL_DRIVER, "org.h2.Driver");
        return driverClassName;
    }

    @Override
    public String getURL() {
        String url = getDbProperties(DB_POOL_URL, "jdbc:openpos:h2:mem:" + getName() + ";DB_CLOSE_ON_EXIT=FALSE");

        final String JDBC_PROTOCOL = "jdbc:";

        if (System.getProperty(SYS_PROP_DISABLE_JUMPMIND_DRIVER) == null
                && !url.startsWith(Driver.DRIVER_PREFIX)
                && url.startsWith(JDBC_PROTOCOL)) {

            String modifiedJdbcUrl = url.replace(JDBC_PROTOCOL, Driver.DRIVER_PREFIX);
            log.info("Modified JDBC URL to include :openpos: protocol. modified='" + modifiedJdbcUrl + "' original= '" + url + "'");
            url = modifiedJdbcUrl;
        }
        if (url.contains("openpos")) {
            loadJumpMindDriver();
        }


        return url;
    }

    protected static void loadJumpMindDriver() {
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public DataSource getDataSource() {
        boolean isOverridden = isNotBlank(env.getProperty(String.format("%s.%s", getName(), DB_POOL_URL)));
        if (dataSource == null || dataSource.getClass().getSimpleName().contains("EmbeddedDataSourceProxy") ||
            isOverridden) {
            this.dataSource = null;
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

                String driverClassName = getDriver();

                properties.put(DB_POOL_DRIVER, driverClassName);
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
                        getName(), driverClassName, properties.get(DB_POOL_URL), properties.get(DB_POOL_USER)));

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
            List<Class<?>> tableExtensionClasses = getClassesForPackageAndAnnotation(packageName, Extends.class);

            if (additionalPackages != null) {
                tableExtensionClasses.addAll(getClassesForPackageAndAnnotation(additionalPackages, Extends.class));
            }

            TypedProperties sessionContext = new TypedProperties();

            sessionContext.put("module.tablePrefix", getTablePrefix());
            sessionContext.put("CREATE_BY", "openpos-" + getName());
            sessionContext.put("LAST_UPDATE_BY", "openpos-" + getName());
            sessionContext.put(DBSession.JDBC_FETCH_SIZE, env.getProperty(DBSession.JDBC_FETCH_SIZE));
            sessionContext.put(DBSession.JDBC_QUERY_TIMEOUT, env.getProperty(DBSession.JDBC_QUERY_TIMEOUT));

            ShadowTablesConfigModel shadowTablesConfig = initializeShadowTables();

            sessionFactory.init(getDatabasePlatform(), sessionContext, tableClasses, tableExtensionClasses, tagHelper, augmenterHelper, clientContext, shadowTablesConfig);
        }

        return sessionFactory;
    }

    protected ShadowTablesConfigModel initializeShadowTables()  {
        ShadowTablesConfigModel shadowTablesConfig = null;
        String shadowTablesDeviceMode = getEnvironmentConfig("db.shadowTables.deviceMode", "");
        String shadowTablePrefix = getEnvironmentConfig("db.shadowTables.tablePrefix", "tng");
        String validateQueries = getEnvironmentConfig("db.shadowTables.validateQueries", "true");

        if (StringUtils.isNotEmpty(shadowTablesDeviceMode) && StringUtils.isNotEmpty(shadowTablePrefix)) {
            List<String> includesList = getEnvironmentConfigList("db.shadowTables.includes");
            List<String> excludesList = getEnvironmentConfigList("db.shadowTables.excludes");

            if (hasShadowTables(getTablePrefix(), includesList)) {
                log.info("Module {} has shadow table(s) for device mode {}", getTablePrefix().toUpperCase(), shadowTablesDeviceMode);
                shadowTablesConfig = new ShadowTablesConfigModel(
                    shadowTablesDeviceMode,
                    shadowTablePrefix,
                    validateQueries.equalsIgnoreCase("true") || validateQueries.equalsIgnoreCase("yes") || validateQueries.equalsIgnoreCase("on"),
                    includesList,
                    getEnvironmentConfigList("db.shadowTables.excludes")
                );
            }
        }

        if (clientContext == null) {
            log.error("Autowired ClientContext is null in {}: Initialization error", this.getClass().getSimpleName());
        }

        return shadowTablesConfig;
    }

    @Override
    public void initialize() {
        updateDataModel(getDBSession());
    }

    public void rebuildDatabase() {
        if (rebuildDatabaseEnabled) {
            List<Table> tables = this.sessionFactory.getTables();
            for (Table table : tables) {
                try {
                    new JdbcTemplate(getDataSource()).execute("drop table " + table.getName());
                } catch(Exception ex) {
                    log.warn("Failed to drop {}.  Reason: {}", table.getName(), ex.getMessage());
                    log.debug("", ex);
                }
            }
            initialize();
        } else {
            log.warn("The rebuild database feature is not enabled");
        }
    }

    public void exportData(String format, String dir, boolean includeModuleTables, String whereClause, List<String> tableFilter, String batchId) {
        List<Table> tables = this.sessionFactory.getTables(includeModuleTables ? new Class<?>[0] : new Class[]{ModuleModel.class});
        if (whereClause == null) {
            whereClause = StringUtils.EMPTY;
        }
        if(StringUtils.isEmpty(batchId)){
            batchId = "01";
        }
        if(tableFilter != null){
        }
        for (Table table : tables) {
            if (
                    (tableFilter == null || tableFilter.stream().anyMatch( t -> t.equalsIgnoreCase(table.getName()))) &&
                    (includeModuleTables || !(
                    table.getName().toLowerCase().endsWith("module") ||
                    table.getName().toLowerCase().endsWith("sample")))
            ){
                if (new JdbcTemplate(dataSource).queryForObject("select count(*) from " + table.getName() + " " + whereClause, Integer.class) > 0) {
                    String filename = String.format("%s_post_01_%s.%s", getVersion(), table.getName().toLowerCase().replaceAll("_", "-"), format.toLowerCase());
                    if(Format.SQL.equals(Format.valueOf(format))){
                        filename = String.format("%s_post_01_%s-%s.%s", getVersion(), batchId, table.getName().toLowerCase().replaceAll("_", "-"), format.toLowerCase());
                    }
                    File out = new File(dir, filename);
                    out.getParentFile().mkdirs();
                    try (OutputStream os = new BufferedOutputStream(
                            new FileOutputStream(out))) {
                        DbExport dbExport = new DbExport(this.databasePlatform);
                        dbExport.setCompatible(Compatible.H2);
                        dbExport.setUseQuotedIdentifiers(false);
                        dbExport.setNoData(false);
                        dbExport.setFormat(Format.valueOf(format));
                        dbExport.setNoCreateInfo(true);
                        dbExport.setWhereClause(whereClause);
                        dbExport.exportTable(os, table.getName(), null);
                    } catch (IOException e) {
                        throw new IoException(e);
                    }
                }
            }

        }
    }

    public void updateDataModel(DBSession session) {
        if (!isDatabaseUpgradeable()) {
            log.info("Module {} database configured as non-upgradeable", getName());
            return;
        }
        String fromVersion = null;

        try {
            ModuleModel info = session.findByNaturalId(ModuleModel.class, installationId);
            if (info != null) {
                fromVersion = info.getCurrentVersion();
            }
        } catch (PersistException e) {
            log.info("The module table is not available");
        }

        String currentVersion = getVersion();
        log.info("The previous version of {} was {} and the current version is {}", getName(),
                fromVersion, currentVersion);

        List<String> profiles = new ArrayList(Arrays.asList(env.getActiveProfiles()));
        if (sqlScriptProfile != null && !sqlScriptProfile.equals("not set") &&
                !profiles.contains(sqlScriptProfile)) {
            profiles.add(sqlScriptProfile);
        }

        List<String> scriptLocations = profiles.stream().map(p->String.format("%s/sql/%s", getName(), p)).collect(Collectors.toList());
        DatabaseScriptContainer scripts = new DatabaseScriptContainer(scriptLocations,
                getDBSession(), installationId);
        IDBSchemaListener schemaListener = getDbSchemaListener();

        scripts.executePreInstallScripts(failStartupOnModuleLoadFailure);
        schemaListener.beforeSchemaCreate(sessionFactory);
        sessionFactory.createAndUpgrade();
        upgradeDbFromXml();
        schemaListener.afterSchemaCreate(sessionFactory);
        scripts.executePostInstallScripts(failStartupOnModuleLoadFailure);

        ModuleModel moduleModel = session.findByNaturalId(ModuleModel.class, installationId);
        if (moduleModel == null) {
            moduleModel = new ModuleModel(installationId, currentVersion);
        } else if (!moduleModel.getCurrentVersion().equals(currentVersion)) {
            moduleModel.setCurrentVersion(currentVersion);
        }
        session.save(moduleModel);
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

    protected boolean isDatabaseUpgradeable() {
        if (loaderConfig == null) {
            return true;
        }
        if (loaderConfig.hasIncludes()) {
            return loaderConfig.includes(getName());
        }
        if (loaderConfig.hasExcludes()) {
            return !loaderConfig.excludes(getName());
        }
        return true;
    }

    private boolean hasShadowTables(String modulePrefix, List<String> includesList)  {
        for (String includeName : includesList)  {
            if (includeName.toLowerCase().startsWith(modulePrefix.toLowerCase()))  {
                return true;
            }
        }
        return false;
    }

    private String getEnvironmentConfig(String configName, String defaultValue) {
        String value = env.getProperty(configName);
        return (value == null ? defaultValue : value);
    }

    private List<String> getEnvironmentConfigList(String configName) {
        Binder binder = new Binder(ConfigurationPropertySources.get(env));
        BindResult<List<String>> val = null;
        // Springboot 2.0 does not allow camelCase, or we could put in dashes
        configName = configName.toLowerCase();
        try {
            val = binder.bind(configName, Bindable.listOf(String.class));
        } catch (Exception e) {
            log.info("ConfigName: '{}' not in environment", configName);
        }

        if (val != null && val.isBound()) {
            return val.get();
        } else {
            log.info("Value for object 'String' could not be bound to list. Config '{}' is not in environment", configName);
            return null;
        }
    }
}
