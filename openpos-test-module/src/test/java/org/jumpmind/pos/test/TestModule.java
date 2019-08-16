package org.jumpmind.pos.test;

import javax.sql.DataSource;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.service.AbstractRDBMSModule;
import org.jumpmind.pos.service.ModuleEnabledCondition;
import org.jumpmind.security.ISecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration("TestModule")
@EnableTransactionManagement
@Conditional(ModuleEnabledCondition.class)
public class TestModule extends AbstractRDBMSModule {
    
    static final String NAME = "test";
    
    private String dynamicVersion = "0.0.1";

    @Override
    public String getName() {
        return NAME;
    }
    
    public void setDynamicVersion(String dynamicVersion) {
        this.dynamicVersion = dynamicVersion;
    }

    @Override
    protected String getArtifactName() {
        return "openpos-test-module";
    }

    @Override
    public String getVersion() {
        return dynamicVersion;
    }

    @Override
    public String getTablePrefix() {
        return "tst";
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

}
