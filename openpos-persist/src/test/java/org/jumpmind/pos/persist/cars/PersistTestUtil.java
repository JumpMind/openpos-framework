package org.jumpmind.pos.persist.cars;

import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.JdbcDatabasePlatformFactory;
import org.jumpmind.db.sql.SqlTemplateSettings;
import org.jumpmind.db.util.BasicDataSourceFactory;
import org.jumpmind.pos.persist.driver.Driver;
import org.jumpmind.properties.TypedProperties;
import org.jumpmind.security.ISecurityService;
import org.jumpmind.security.SecurityServiceFactory;

@Slf4j
public class PersistTestUtil {

    public static IDatabasePlatform testDbPlatform() {
        return JdbcDatabasePlatformFactory.createNewPlatformInstance(testDataSource(), new SqlTemplateSettings(), false, false);
    }
    
    static ISecurityService securityService() {
        return SecurityServiceFactory.create();
    }      
    
    public static BasicDataSource testDataSource() {
        return BasicDataSourceFactory.create(getH2TestProperties(), securityService());
    }    

    static TypedProperties getH2TestProperties() {
        Properties props = new Properties();
        
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException ex) {
            log.error("Could not load Driver", ex);
        }
 
        props.setProperty("db.url", "jdbc:h2:mem:open-pos-test;LOCK_TIMEOUT=60000;DB_CLOSE_DELAY=-1");
        props.setProperty("db.user", "");
        props.setProperty("db.password", "");
        props.setProperty("db.driver", "org.h2.Driver");

        return new TypedProperties(props);
    }
    
    public static TypedProperties getSessionContext() {
        TypedProperties sessionContext = new TypedProperties();
        sessionContext.put("module.tablePrefix", "car");
        sessionContext.put("CREATE_BY", "unit-test");
        sessionContext.put("LAST_UPDATE_BY", "unit-test");
        return sessionContext;
    }
    

}

    
