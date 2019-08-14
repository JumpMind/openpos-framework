package org.jumpmind.pos.service;

import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.security.ISecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public interface IRDBMSModule {

    DataSource getDataSource();
    DBSession getDBSession();
    PlatformTransactionManager getPlatformTransactionManager();
    IDatabasePlatform getDatabasePlatform();

}
