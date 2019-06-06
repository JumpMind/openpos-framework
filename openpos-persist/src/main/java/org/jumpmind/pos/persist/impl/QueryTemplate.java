package org.jumpmind.pos.persist.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jumpmind.pos.persist.PersistException;
import org.jumpmind.pos.persist.Query;
import org.jumpmind.pos.persist.SqlStatement;
import org.springframework.util.Assert;

public class QueryTemplate implements Cloneable {

    private String name;
    private String select;
    private List<String> optionalWhereClauses;
    private String groupBy;
    private String where;
    private String orderBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setOptionalWhereClauses(List<String> optionalWhereClauses) {
        this.optionalWhereClauses = optionalWhereClauses;
    }
    
    public void setSelect(String select) {
        this.select = select;
    }
    
    public String getSelect() {
        return select;
    }    
    
    public List<String> getOptionalWhereClauses() {
        if (optionalWhereClauses == null) {
            optionalWhereClauses = new ArrayList<>();
        }
        return optionalWhereClauses;
    }

    public QueryTemplate optionalWhere(String optionalWhere) {
        this.getOptionalWhereClauses().add(optionalWhere);
        return this;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public QueryTemplate copy() {
        try {
            return (QueryTemplate)this.clone();
        } catch (CloneNotSupportedException e) {
            throw new PersistException(e);
        }
    }
    
    public SqlStatement generateSQL(Query<?> query, Object singleParam) {
        Map<String, Object> params = new HashMap<>();
        params.put("*", singleParam);
        return generateSQL(query, params);
    }

    public SqlStatement generateSQL(Query<?> query, Map<String, Object> params) {
        String select = this.getSelect();
        List<String> keys = new ArrayList<>();

        StringSubstitutor literalSubtitution = new StringSubstitutor(new StringLookup() {
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
                return "?";
            }
        });

        String preppedSelectClause = literalSubtitution.replace(select);
        preppedSelectClause = sub.replace(preppedSelectClause);

        String preppedWhereClause = literalSubtitution.replace(this.getWhere());
        preppedWhereClause = sub.replace(preppedWhereClause);

        StringBuilder buff = new StringBuilder();
        preppedSelectClause = stripWhere(preppedSelectClause);

        boolean hasWhereKeyword = false;

        buff.append(preppedSelectClause);
        if (!StringUtils.isEmpty(preppedWhereClause)) {
            hasWhereKeyword = true;
            buff.append(" WHERE ");
            buff.append(preppedWhereClause);
        }

        boolean firstIncluded = true;
        for (String optionalWhereClause : this.getOptionalWhereClauses()) {
            Set<String> optionalWhereClauseKeys = new LinkedHashSet<>();
            String preppedOptionalWhereClause = literalSubtitution.replace(optionalWhereClause);

            StringSubstitutor optionalSubstitution = new StringSubstitutor(new StringLookup() {
                @Override
                public String lookup(String key) {
                    optionalWhereClauseKeys.add(key);
                    return "?";
                }
            });

            preppedOptionalWhereClause = optionalSubstitution.replace(preppedOptionalWhereClause);

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
                if (query.isUseAnd() || firstIncluded) {
                    buff.append(" AND (");
                } else {
                    buff.append(" OR (");
                }
                buff.append(preppedOptionalWhereClause);
                buff.append(")");
                keys.addAll(optionalWhereClauseKeys);
            }
            
            if (shouldInclude) {
               firstIncluded = false;
            }
        }

        if (!StringUtils.isEmpty(this.getGroupBy())) {
            buff.append(" GROUP BY ");
            buff.append(this.getGroupBy());
        }

        if (!StringUtils.isEmpty(this.getOrderBy())) {
            buff.append(" ORDER BY ");
            buff.append(this.getOrderBy());
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
                if (params.containsKey(key)) {
                    throw new PersistException(String.format(
                            "Required query parameter '%s' was present but the value is null. A value must be provided. Cannot build query: %s",
                            key, sqlStatement.getSql()));
                } else {
                    throw new PersistException(
                            String.format("Missing required query parameter '%s'. Cannot build query: %s", key, sqlStatement.getSql()));
                }
            }
            values.add(value);
        }

        sqlStatement.setValues(values);
        return sqlStatement;
    }

    protected String stripWhere(String preppedSelectClause) {
        Assert.notNull(preppedSelectClause, "preppedSelectClause must be non-null.");
        String sqlTrimmed = preppedSelectClause.trim();
        if (sqlTrimmed.endsWith("WHERE") || sqlTrimmed.endsWith("where")) {
            return sqlTrimmed.substring(0, sqlTrimmed.length() - "where".length());
        } else {
            return sqlTrimmed;
        }
    }

    

}
