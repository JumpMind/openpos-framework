package org.jumpmind.pos.persist;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlStatement {
    
    private String sql;
    private MapSqlParameterSource parameters;
    //private List<Integer> types = new ArrayList<>();
    
    public String getSql() {
        return sql;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }

    public MapSqlParameterSource getParameters() {
        if (this.parameters == null) {
            this.parameters = new MapSqlParameterSource();
        }
        return this.parameters;
    }

    public void setParameters(Map<String,Object> params) {
        this.parameters = new MapSqlParameterSource(params);
    }


}
