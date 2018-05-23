package org.jumpmind.pos.persist.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Transaction {
    
    private Date startTime = new Date();
    private Map<String, Object> entities = new LinkedHashMap<String, Object>();
    
    public void save(String id, Object entity) {
        entities.put(id, entity);
        // TODO actually perform the appropriate insert/update.
    }
    
    public void rollback() {
        // TODO actually rollback the transaction on the connection.
    }

    @Override
    public String toString() {
        return "Transaction [startTime=" + startTime + ", entities=" + entities + "]";
    }

}
