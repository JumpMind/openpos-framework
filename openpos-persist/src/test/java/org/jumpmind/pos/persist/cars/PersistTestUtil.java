package org.jumpmind.pos.persist.cars;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.JdbcDatabasePlatformFactory;
import org.jumpmind.db.sql.SqlTemplateSettings;
import org.jumpmind.db.util.BasicDataSourceFactory;
import org.jumpmind.pos.persist.driver.Driver;
import org.jumpmind.properties.TypedProperties;
import org.jumpmind.security.ISecurityService;
import org.jumpmind.security.SecurityServiceFactory;

public class PersistTestUtil {
    
    private static Logger log = Logger.getLogger(PersistTestUtil.class);
    
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
        String dbDir = System.getProperty("user.dir") + "/build/open-pos-test";
        
        try {
            Class.forName(Driver.class.getName());
        } catch (ClassNotFoundException ex) {
            log.error("Could not load Driver", ex);
        }
 
        props.setProperty("db.url", "jdbc:openpos:h2:mem:open-pos-test;LOCK_TIMEOUT=60000;DB_CLOSE_DELAY=-1");
        props.setProperty("db.user", "");
        props.setProperty("db.password", "");
        props.setProperty("db.driver", "org.h2.Driver");

        return new TypedProperties(props);
    }
    
    public static Map<String, String> getSessionContext() {
        Map<String, String> sessionContext = new HashMap<>();
        sessionContext.put("module.tablePrefix", "car");
        sessionContext.put("CREATE_BY", "unit-test");
        sessionContext.put("LAST_UPDATE_BY", "unit-test");
        return sessionContext;
    }
    

}

    
