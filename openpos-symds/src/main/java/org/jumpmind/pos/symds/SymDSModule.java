package org.jumpmind.pos.symds;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.db.model.Table;
import org.jumpmind.pos.core.flow.StateManager;
import org.jumpmind.pos.core.flow.StateManagerContainer;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.service.AbstractRDBMSModule;
import org.jumpmind.pos.service.ModuleEnabledCondition;
import org.jumpmind.security.ISecurityService;
import org.jumpmind.symmetric.common.Constants;
import org.jumpmind.symmetric.common.ParameterConstants;
import org.jumpmind.symmetric.io.data.Batch;
import org.jumpmind.symmetric.io.data.CsvData;
import org.jumpmind.symmetric.io.data.DataContext;
import org.jumpmind.symmetric.io.data.DataEventType;
import org.jumpmind.symmetric.io.data.writer.DatabaseWriterFilterAdapter;
import org.jumpmind.symmetric.io.data.writer.IDatabaseWriterErrorHandler;
import org.jumpmind.symmetric.service.INodeService;
import org.jumpmind.symmetric.web.ServerSymmetricEngine;
import org.jumpmind.symmetric.web.SymmetricEngineHolder;
import org.jumpmind.symmetric.web.SymmetricServlet;
import org.jumpmind.symmetric.web.WebConstants;
import org.jumpmind.util.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import static org.jumpmind.symmetric.common.Constants.*;

@Configuration("SymDSModule")
@EnableTransactionManagement
@Conditional(ModuleEnabledCondition.class)
@Order(20000)
@Slf4j
public class SymDSModule extends AbstractRDBMSModule {

    public static final String CHANNEL_OPS = "ops";

    public final static String NAME = "sym";
    
    @Autowired
    ServletContext context;

    @Autowired
    CacheManager cacheManager;

    ServerSymmetricEngine serverEngine;

    @Value("${openpos.installationId:'not set'}")
    String installationId;

    @Autowired
    Environment env;

    @Autowired
    List<ISymDSConfigurator> configurators;

    @Autowired
    StateManagerContainer stateManagerContainer;

    @Override
    public void initialize() {
            SymmetricEngineHolder holder = new SymmetricEngineHolder();
            Properties properties = new Properties();

            configurators.forEach(c -> c.beforeCreate(properties));

            serverEngine = new ServerSymmetricEngine(getDataSource(), applicationContext, properties, false, holder);
            serverEngine.getExtensionService().addExtensionPoint(new DatabaseWriterFilterAdapter() {
                @Override
                public void batchCommitted(DataContext context) {
                    Batch batch = context.getBatch();
                    if (CHANNEL_RELOAD.equals(batch.getChannelId())) {
                        Collection<String> names = cacheManager.getCacheNames();
                        for (String name : names) {
                            cacheManager.getCache(name).clear();
                        }
                    }
                }

                @Override
                public void afterWrite(DataContext context, Table table, CsvData data) {
                    Batch batch = context.getBatch();
                    if (CHANNEL_OPS.equals(batch.getChannelId())) {
                        clearOpsCacheData(context, table, data);
                    }
                }
            });
            holder.getEngines().put(properties.getProperty(ParameterConstants.EXTERNAL_ID), serverEngine);
            holder.setAutoStart(false);
            context.setAttribute(WebConstants.ATTR_ENGINE_HOLDER, holder);

            configurators.forEach(c -> c.beforeStart(serverEngine));

        if ("true".equals(env.getProperty("openpos.symmetric.start", "false"))) {
            serverEngine.setup();
        } else {
            serverEngine.setupDatabase(false);
        }

        super.initialize();

    }

    protected void clearOpsCacheData(DataContext context, Table table, CsvData data) {
        if (table != null && table.getName() != null
                && table.getName().toUpperCase().equals("OPS_UNIT_STATUS")) {

            int businessUnitColumnIndex = table.getColumnIndex("BUSINESS_UNIT_ID");
            if (businessUnitColumnIndex != -1) {
                String[] rowData = data.getParsedData("rowData");
                if (rowData != null && rowData.length > businessUnitColumnIndex) {
                    String batchBusinessUnitId = rowData[businessUnitColumnIndex];
                    clearBusinessDates(batchBusinessUnitId);
                }
            }
        }
    }

    protected void clearBusinessDates(String batchBusinessUnitId) {
        List<StateManager> stateManagers = stateManagerContainer.getAllStateManagers();
        for (StateManager stateManager : stateManagers) {
            if (stateManager.getDeviceId().startsWith(batchBusinessUnitId)) {
                log.info("Removing business date from device scope (cache invalidation) due to incoming data for device " + stateManager.getDeviceId());
                stateManager.getApplicationState().getScope().removeDeviceScope("businessDate");
            }
        }
    }

    @Override
    public void start() {
        if ("true".equals(env.getProperty("openpos.symmetric.start", "false"))) {
            serverEngine.start();

            if ("true".equals(env.getProperty("openpos.symmetric.waitForInitialLoad", "false"))) {
               waitForInitialLoad();
            }
        }
        super.start();
    }

    protected void waitForInitialLoad() {
        try {
            INodeService nodeService = serverEngine.getNodeService();
            MonitorFilter monitor = new MonitorFilter();
            serverEngine.getExtensionService().addExtensionPoint(monitor);

            long ts = System.currentTimeMillis();
            final DateFormat DATE_FORMAT = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM);
            if (!nodeService.isDataLoadCompleted()) {
                log.info(String.format("SymmetricDS initial data load started at %s", DATE_FORMAT.format(new Date())));
            }

            int tableCount = 0;

            boolean initialLoaded = false;

            Exception error = null;
            while (!nodeService.isDataLoadCompleted()) {
                initialLoaded = true;
                while (monitor.tableQueue.size() > 0) {
                    printTable(monitor.tableQueue.remove(0), ++tableCount);
                }

                if ((error == null && monitor.error != null) || (error != null && monitor.error != null && !error.equals(monitor.error))) {
                    error = monitor.error;
                    log.error("An error occured", error);
                }

                AppUtils.sleep(500);
            }

            while (monitor.tableQueue.size() > 0) {
                printTable(monitor.tableQueue.remove(0), ++tableCount);
            }

            serverEngine.getExtensionService().removeExtensionPoint(monitor);

            if (initialLoaded && nodeService.isDataLoadCompleted()) {
                log.info(String.format("SymmetricDS initial data load complete at %s in %s seconds", DATE_FORMAT.format(new Date()),
                        ((System.currentTimeMillis() - ts) / 1000)));
            } else {
                log.info("No tables were initial loaded");
            }


        } catch (Exception e) {
            log.error("Failure while waiting for initial load", e);
        }
    }

    void printTable(String currentTable, int tableCount) {
        log.info("Loading " + currentTable + ".  Current table count=" + tableCount);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected String getArtifactName() {
        return "nu-symds";
    }

    @Override
    public String getTablePrefix() {
        return NAME;
    }

    @Bean
    public ServletRegistrationBean<SymmetricServlet> symServlet() {
        ServletRegistrationBean<SymmetricServlet> bean = new ServletRegistrationBean<>(new SymmetricServlet(), configurators.get(configurators.size()-1).getWebContext());
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Override
    @Bean(name = NAME + "TxManager")
    public PlatformTransactionManager getPlatformTransactionManager() {
        return super.getPlatformTransactionManager();
    }

    @Override
    @Bean(name = NAME + "SecurityService")
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
    protected ISecurityService securityService() {
        return super.securityService();
    }

    @Override
    @Bean(name = NAME + "DataSource")
    public DataSource getDataSource() {
        return super.getDataSource();
    }

    @Override
    @Bean(name = NAME + "SessionFactory")
    protected DBSessionFactory sessionFactory() {
        return super.sessionFactory();
    }

    @Override
    @Bean(name = NAME + "Session")
    public DBSession getDBSession() {
        return super.getDBSession();
    }

    static class MonitorFilter extends DatabaseWriterFilterAdapter implements IDatabaseWriterErrorHandler {

        Set<String> tablesEncountered = new HashSet<String>();

        List<String> tableQueue = Collections.synchronizedList(new ArrayList<String>());

        Exception error;

        @Override
        public boolean beforeWrite(DataContext context, Table table, CsvData data) {
            error = null;
            if (context.getBatch().getChannelId().equals(Constants.CHANNEL_RELOAD) && !data.getDataEventType().equals(DataEventType.SQL)) {
                if (!tablesEncountered.contains(table.getName())) {
                    tablesEncountered.add(table.getName());
                    tableQueue.add(table.getName());
                }
            }
            return true;
        }

        @Override
        public boolean handleError(DataContext context, Table table, CsvData data, Exception ex) {
            error = ex;
            return true;
        }
    }

}
