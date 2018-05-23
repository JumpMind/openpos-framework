package org.jumpmind.pos.persist;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jumpmind.pos.persist.impl.QueryTemplate;
import org.jumpmind.pos.persist.impl.QueryTemplates;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class Query<T> {
    
    private Class<? extends T> resultClass;
    private String name;
    private QueryTemplate queryTemplate;
    
    
//    private String selectSql; // Optional if target is an Entity.
//    private List<String> optionalWhereClauses = new ArrayList<>();
//    private String groupByClause;
//    private String whereClause;
//    private String orderByClause;

    public Query<T> result(Class<? extends T> resultClass) {
        this.resultClass = resultClass;
        return this;
    }
    
//    public Query<T> select(String selectSql) {
//        this.selectSql = selectSql;
//        return this;
//    }
//    
//    public Query<T> where(String whereClause) {
//        this.whereClause = whereClause;
//        return this;
//    }
//    
//    public Query<T> orderBy(String orderByClause) {
//        this.orderByClause = orderByClause;
//        return this;
//    }                
//    
//    public Query<T> optionalWhere(String whereClause) {
//        optionalWhereClauses.add(whereClause);
//        return this;
//    }

    public Class<? extends T> getResultClass() {
        return resultClass;
    }

    public void setResultClass(Class<? extends T> resultClass) {
        this.resultClass = resultClass;
    }

//    public String getSelectSql() {
//        return selectSql;
//    }
//
//    public void setSelectSql(String selectSql) {
//        this.selectSql = selectSql;
//    }
//
//    public String getWhereClause() {
//        return whereClause;
//    }
//
//    public void setWhereClause(String whereClause) {
//        this.whereClause = whereClause;
//    }
    
    public SqlStatement generateSQL(Map<String, Object> params) {
        List<String> keys = new ArrayList<>();
        
        StringSubstitutor sub = new StringSubstitutor(new StringLookup() {
            @Override
            public String lookup(String key) {
                keys.add(key);
                return "?";
            }
        });
        
        String preppedSelectClause = sub.replace(queryTemplate.getSelect());
        String preppedWhereClause = sub.replace(queryTemplate.getWhere());
        
        StringBuilder buff = new StringBuilder();
        preppedSelectClause = stripWhere(preppedSelectClause);
        
        boolean hasWhereKeyword = false;
        
        buff.append(preppedSelectClause);
        if (!StringUtils.isEmpty(preppedWhereClause)) {
            hasWhereKeyword = true;
            buff.append(" WHERE ");
            buff.append(preppedWhereClause);    
        } 
        
        for (String optionalWhereClause : queryTemplate.getOptionalWhereClauses()) {
            List<String> optionalWhereClauseKeys = new ArrayList<>();
            StringSubstitutor optionalSubstitution = new StringSubstitutor(new StringLookup() {
                @Override
                public String lookup(String key) {
                    optionalWhereClauseKeys.add(key);
                    return "?";
                }
            });
            String preppedOptionalWhereClause = optionalSubstitution.replace(optionalWhereClause);
            
            boolean shouldInclude = true;
            for (String key : optionalWhereClauseKeys) {
                if (!params.containsKey(key)) {
                    shouldInclude = false;
                    break;
                }
            }
            
            if (shouldInclude) {
                if (!hasWhereKeyword) {
                    buff.append(" WHERE 1=1 ");    
                    hasWhereKeyword = true;
                }
                buff.append(" AND (");
                buff.append(preppedOptionalWhereClause);
                buff.append(")");
                keys.addAll(optionalWhereClauseKeys);
            }
        }
        
        if (!StringUtils.isEmpty(queryTemplate.getGroupBy())) {
            buff.append(" GROUP BY ");
            buff.append(queryTemplate.getGroupBy());    
        }
        
        if (!StringUtils.isEmpty(queryTemplate.getOrderBy())) {
            buff.append(" ORDER BY ");
            buff.append(queryTemplate.getOrderBy());    
        }
        
        SqlStatement sqlStatement = new SqlStatement();
        sqlStatement.setSql(buff.toString());
        
        List<Object> values = new ArrayList<>();
        for (String key : keys) {
            Object value = params.get(key);
            if (value == null) {
                value = params.get("*");
            }
            if (value == null) {
                throw new PersistException(String.format("Missing required query parameter '%s'. Cannot build query: %s", key, sqlStatement.getSql()));
            }
            values.add(value);
        }
        
        sqlStatement.setValues(values);
        return sqlStatement;
    }
    
    protected String stripWhere(String preppedSelectClause) {
        String sqlTrimmed = preppedSelectClause.trim();
        if (sqlTrimmed.endsWith("WHERE") || sqlTrimmed.endsWith("where")) {
            return sqlTrimmed.substring(0, sqlTrimmed.length()-"where".length());
        } else {
            return sqlTrimmed;
        }
    }

    public SqlStatement generateSQL(Object singleParam) {
        Map<String, Object> params = new HashMap<>();
        params.put("*", singleParam);
        return generateSQL(params);
    }

    public Query<T> named(String name) {
        this.name = name;
        return this;
    }

    public QueryTemplate getQueryTemplate() {
        return queryTemplate;
    }

    public void setQueryTemplate(QueryTemplate queryTemplate) {
        this.queryTemplate = queryTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
//    public Query<T> groupBy(String groupByClause) {
//        this.groupByClause = groupByClause;
//        return this;
//    }
    
//    public String generateSQL(Object singleParam) {
//        StringSubstitutor sub = new StringSubstitutor(new StringLookup() {
//            @Override
//            public String lookup(String key) {
//                return "?";
//            }
//        });
//        String preppedWhereClause = sub.replace(whereClause);
//        
//        StringBuilder buff = new StringBuilder();
//        buff.append(selectSql);
//        if (!StringUtils.isEmpty(preppedWhereClause)) {
//            buff.append(preppedWhereClause);    
//        }
//        if (!StringUtils.isEmpty(orderByClause)) {
//            buff.append(" ORDER BY ");
//            buff.append(orderByClause);    
//        }
//        
//        return buff.toString();
//    }
    
    

}
