package org.jumpmind.pos.devices;

import javax.sql.DataSource;

import org.jumpmind.pos.devices.service.IDevicesService;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.service.AbstractModule;
import org.jumpmind.pos.service.ModuleEnabledCondition;
import org.jumpmind.security.ISecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration("DevicesModule")
@EnableTransactionManagement
@Conditional(ModuleEnabledCondition.class)
@Order(10)
public class DevicesModule extends AbstractModule {
    
    protected final static String NAME = "dev";
    
    public DevicesModule() {
    }
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected String getArtifactName() {
        return "openpos-devices";
    }

    @Override
    public String getTablePrefix() {
        return NAME;
    }
    
    @Override
    @Bean(name = NAME + "TxManager")
    protected PlatformTransactionManager txManager() {
        return super.txManager();
    }

    @Override
    @Bean(name = NAME + "SecurityService")
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
    protected ISecurityService securityService() {
        return super.securityService();
    }

    @Override
    @Bean(name = NAME + "DataSource")
    protected DataSource dataSource() {
        return super.dataSource();
    }

    @Override
    @Bean(name = NAME + "SessionFactory")
    protected DBSessionFactory sessionFactory() {
        return super.sessionFactory();
    }

    @Override
    @Bean(name = NAME + "Session")
    protected DBSession session() {
        return super.session();
    }   
    
    @Bean
    protected IDevicesService devicesService() {
        return this.buildService(IDevicesService.class);
    }
        
}
