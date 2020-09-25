package org.jumpmind.pos.devices;

import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.pos.devices.service.IDevicesService;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.service.AbstractRDBMSModule;
import org.jumpmind.pos.service.ModuleEnabledCondition;
import org.jumpmind.security.ISecurityService;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration("DevicesModule")
@EnableTransactionManagement()
@Conditional(ModuleEnabledCondition.class)
@Order(10)
public class DevicesModule extends AbstractRDBMSModule {

    public final String NAME = "dev";

    IDevicesService devicesService;

    @Override
    protected String getArtifactName() {
        return "openpos-devices";
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getTablePrefix() {
        return NAME;
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
    @Bean(name = NAME + "SessionFactory")
    protected DBSessionFactory sessionFactory() {
        return super.sessionFactory();
    }

    @Override
    @Bean(name = NAME + "Session")
    public DBSession getDBSession() {
        return super.getDBSession();
    }

    @Override
    @Bean(name = NAME + "DatabasePlatform")
    public IDatabasePlatform getDatabasePlatform() {
        return super.getDatabasePlatform();
    }

    @Bean
    protected IDevicesService devicesService() {
        if( devicesService == null ){
            devicesService = buildService(IDevicesService.class);
        }

        return devicesService;
    }
}