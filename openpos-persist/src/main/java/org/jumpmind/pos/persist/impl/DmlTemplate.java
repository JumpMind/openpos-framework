package org.jumpmind.pos.persist.impl;

import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jumpmind.pos.persist.PersistException;
import org.jumpmind.pos.persist.Query;
import org.jumpmind.pos.persist.SqlStatement;
import org.jumpmind.pos.util.model.AbstractTypeCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DmlTemplate implements Cloneable {

    String name;
    String dml;
    
    public void setDml(String dml) {
        this.dml = dml;
    }
    
    public String getDml() {
        return dml;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public SqlStatement generateSQL(String sql, Map<String, Object> params) {
        List<String> keys = new ArrayList<>();
        StringSubstitutor literalSubstitution = new StringSubstitutor(new StringLookup() {
            @Override
            public String lookup(String key) {
                Object paramValue = params.get(key);
                return paramValue != null ? paramValue.toString() : "null";
            }
        }, "$${", "}", '\\');

        StringSubstitutor sub = new StringSubstitutor(new StringLookup() {
            @Override
            public String lookup(String key) {
                keys.add(key);
                return ":" + key;
            }
        });

        String preparedSql = literalSubstitution.replace(sql);
        preparedSql = sub.replace(preparedSql);

        SqlStatement sqlStatement = new SqlStatement();
        sqlStatement.setSql(preparedSql);
        for (String key : keys) {
            Object value = params.get(key);
            if (value == null) {
                value = params.get("*");
                params.put(key, value);
            }
            if (value == null) {
                if (params.containsKey(key)) {
                    throw new PersistException(String.format(
                            "Required query parameter '%s' was present but the value is null. A value must be provided. Cannot build query: %s",
                            key, sqlStatement.getSql()));
                } else {
                    throw new PersistException(
                            String.format("Missing required query parameter '%s'. Cannot build query: %s", key, sqlStatement.getSql()));
                }
            } else if (value instanceof Boolean) {
                boolean bool = (Boolean)value;
                value = bool ? 1 : 0;
                params.put(key, value);
            } else if (value instanceof AbstractTypeCode) {
                value = ((AbstractTypeCode)value).value();
                params.put(key, value);
            }
        }
        if (params != null) {
            params.remove("*");
        }
        sqlStatement.setParameters(params);
        return sqlStatement;
    }

    public DmlTemplate copy() {
        try {
            return (DmlTemplate)this.clone();
        } catch (CloneNotSupportedException e) {
            throw new PersistException(e);
        }
    }

}
