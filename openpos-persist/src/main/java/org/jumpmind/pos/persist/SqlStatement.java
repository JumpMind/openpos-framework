package org.jumpmind.pos.persist;

import java.util.ArrayList;
import java.util.List;

public class SqlStatement {
    
    private String sql;
    private List<Object> values = new ArrayList<>();
    private List<Integer> types = new ArrayList<>();
    
    public String getSql() {
        return sql;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }
    public List<Object> getValues() {
        return values;
    }
    public void setValues(List<Object> values) {
        this.values = values;
    }
    public List<Integer> getTypes() {
        return types;
    }
    public void setTypes(List<Integer> types) {
        this.types = types;
    }

}
