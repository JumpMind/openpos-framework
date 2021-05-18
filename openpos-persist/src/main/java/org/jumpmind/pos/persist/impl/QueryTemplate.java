package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jumpmind.pos.persist.PersistException;
import org.jumpmind.pos.persist.Query;
import org.jumpmind.pos.persist.SqlStatement;
import org.jumpmind.pos.util.model.AbstractTypeCode;
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

    public void setSelect(String select) {
        this.select = select;
    }
    
    public String getSelect() {
        return select;
    }

    public boolean hasSelect()  { return (this.select != null); }

    public List<String> getOptionalWhereClauses() {
        if (optionalWhereClauses == null) {
            optionalWhereClauses = new ArrayList<>();
        }
        return optionalWhereClauses;
    }

    public void setOptionalWhereClauses(List<String> optionalWhereClauses) {
        this.optionalWhereClauses = optionalWhereClauses;
    }

    public boolean hasOptionalWhereClauses()  { return !getOptionalWhereClauses().isEmpty(); }

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

    public boolean hasGroupBy()  { return (this.groupBy != null); }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public boolean hasWhere()  { return (this.where != null); }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean hasOrderBy()  { return (this.orderBy != null); }

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
                return ":" + key;
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
                    return ":" + key;
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

        splitTooManyValuesInClause(query, params, buff);

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

    protected String stripWhere(String preppedSelectClause) {
        Assert.notNull(preppedSelectClause, "preppedSelectClause must be non-null.");
        String sqlTrimmed = preppedSelectClause.trim();
        if (sqlTrimmed.endsWith("WHERE") || sqlTrimmed.endsWith("where")) {
            return sqlTrimmed.substring(0, sqlTrimmed.length() - "where".length());
        } else {
            return sqlTrimmed;
        }
    }

    private void splitTooManyValuesInClause(Query<?> query, Map<String, Object> params, StringBuilder buffer) {
        if (MapUtils.isNotEmpty(params)) {
            Map<String, Object> newParams = new HashMap<>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (getSize(entry.getValue()) > query.getMaxInParameters()) {
                    newParams.putAll(splitInClause(entry, buffer, query));
                }
            }
            params.putAll(newParams);
        }
    }


    private Map<String, Object> splitInClause(Map.Entry<String, Object> entry, StringBuilder buffer, Query<?> query) {
        Map<String, Object> newParams = new HashMap<>();
        Pattern pattern = Pattern.compile("([\\w.$*@]+\\s+(not\\s+)?in\\s*\\(\\s*)(:" + Pattern.quote(entry.getKey()) + ")(\\s*\\))", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(buffer);
        Map<Integer, ? extends List> indexToList = partitionList(entry.getValue(), query.getMaxInParameters());
        while (matcher.find()) {
            StringBuilder replacement = new StringBuilder();
            replacement.append("(");
            int parameterIndex = 0;
            for (Map.Entry<Integer, ? extends List> indexEntry : indexToList.entrySet()) {
                if (indexEntry.getKey() > 0) {
                    if (matcher.group(2) != null) {
                        replacement.append(" AND ");
                    }
                    else {
                        replacement.append(" OR ");
                    }
                }
                replacement.append(matcher.group(1));
                List<?> entries = indexEntry.getValue();
                for (int i = 0; i < entries.size(); i++) {
                    replacement.append(matcher.group(3)).append("$").append(parameterIndex);

                    newParams.put(entry.getKey() + "$" + parameterIndex, entries.get(i));
                    if(i != entries.size() - 1) {
                        replacement.append(",");
                    }
                    parameterIndex++;
                }
                replacement.append(matcher.group(4));
            }
            replacement.append(")");
            buffer.replace(matcher.start(), matcher.end(), replacement.toString());
        }
        return newParams;
    }

    private Map<Integer, ? extends List> partitionList(Object object, int chunkSize) {
        AtomicInteger counter = new AtomicInteger();
        Stream<?> stream = null;
        if (object instanceof Collection) {
            stream = ((Collection) object).stream();
        }
        if (object.getClass().isArray()) {
            stream = Arrays.stream((Object[]) object);
        }
        if (stream != null) {
            return stream.collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize));
        }
        return Collections.emptyMap();
    }

    private int getSize(Object paramValue) {
        if (paramValue == null) {
            return 0;
        }
        if (paramValue instanceof Collection) {
            return ((Collection) paramValue).size();
        }
        if (paramValue.getClass().isArray()) {
            return Array.getLength(paramValue);
        }
        return 1;
    }
}
