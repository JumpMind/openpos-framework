package org.jumpmind.pos.persist.impl.dialect;


public class H2Dialect implements IDbDialect {

    @Override
    public String getDefaultValidationQuery() {
        return "select 1";
    }
    
}
