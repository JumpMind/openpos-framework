package org.jumpmind.pos.user;

import org.apache.commons.dbcp.BasicDataSource;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.service.AbstractModule;
import org.jumpmind.pos.service.Module;
import org.jumpmind.security.ISecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration("UserModule")
@EnableTransactionManagement
public class UserModule extends AbstractModule implements Module {

    static final String NAME = "user";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public String getTablePrefix() {
        return "usr";
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
    protected BasicDataSource dataSource() {
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
