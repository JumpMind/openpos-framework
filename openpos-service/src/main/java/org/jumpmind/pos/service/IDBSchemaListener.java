package org.jumpmind.pos.service;

import org.jumpmind.pos.persist.DBSessionFactory;

public interface IDBSchemaListener {

    public void beforeSchemaCreate(DBSessionFactory sessionFactory);
    public void afterSchemaCreate(DBSessionFactory sessionFactory);
    
}
