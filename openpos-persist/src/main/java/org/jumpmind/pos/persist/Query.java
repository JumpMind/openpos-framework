package org.jumpmind.pos.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jumpmind.pos.persist.impl.QueryTemplate;

public class Query<T> {

    private Class<? extends T> resultClass;
    private QueryTemplate queryTemplate = new QueryTemplate();

    public Query<T> result(Class<? extends T> resultClass) {
        this.resultClass = resultClass;
        return this;
    }

    public Class<? extends T> getResultClass() {
        return resultClass;
    }

    public void setResultClass(Class<? extends T> resultClass) {
        this.resultClass = resultClass;
    }

    public SqlStatement generateSQL(Map<String, Object> params) {
        String select = queryTemplate.getSelect();
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

        String preppedWhereClause = literalSubtitution.replace(queryTemplate.getWhere());
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

        for (String optionalWhereClause : queryTemplate.getOptionalWhereClauses()) {
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
        String sqlTrimmed = preppedSelectClause.trim();
        if (sqlTrimmed.endsWith("WHERE") || sqlTrimmed.endsWith("where")) {
            return sqlTrimmed.substring(0, sqlTrimmed.length() - "where".length());
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
        this.queryTemplate.setName(name);
        return this;
    }

    public String getName() {
        return this.queryTemplate != null ? this.queryTemplate.getName() : null;
    }

    public QueryTemplate getQueryTemplate() {
        return queryTemplate;
    }

    public void setQueryTemplate(QueryTemplate queryTemplate) {
        this.queryTemplate = queryTemplate;
    }

}
