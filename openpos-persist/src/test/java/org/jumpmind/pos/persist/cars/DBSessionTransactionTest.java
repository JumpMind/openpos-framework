package org.jumpmind.pos.persist.cars;

import java.util.Arrays;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.PersistException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DBSessionTransactionTest {
    
    private DBSessionFactory sessionFactory = new DBSessionFactory();

    @Before
    public void setup() {
        sessionFactory.init(
                PersistTestUtil.testDbPlatform(),
                PersistTestUtil.getSessionContext(), 
                Arrays.asList(CarEntity.class),
                DBSessionFactory.getQueryTempaltes("test"));                
    }
    
    @Test(expected = PersistException.class)
    public void testInvalidStartTransaction() {
        DBSession db = sessionFactory.createDbSession();
        db.startTransaction();
        db.startTransaction();
    }
    
    @Test(expected = PersistException.class)
    public void testInvalidCommit() {
        DBSession db = sessionFactory.createDbSession();
        db.commitTransaction();
    }
    
    @Test(expected = PersistException.class)
    public void testInvalidRollback() {
        DBSession db = sessionFactory.createDbSession();
        db.rollbackTransaction();
    }
    
    @Test(expected = PersistException.class)
    public void testEmptyTransaction() {
        DBSession db = sessionFactory.createDbSession();
        db.startTransaction();
        db.commitTransaction();
        db.startTransaction();
        db.rollbackTransaction();
    }
    
}
