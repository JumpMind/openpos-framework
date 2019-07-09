package org.jumpmind.pos.persist.model;

import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.persist.AbstractModel;

public class SearchCriteria {

    Map<String, Object> criteria;

    Class<? extends AbstractModel> entityClass;

    boolean useAnd = true;

    int maxResults = 1000;

    public SearchCriteria() {

    }

    public SearchCriteria(Class<? extends AbstractModel> entityClass) {
        this.entityClass = entityClass;
    }

    public SearchCriteria(Map<String, Object> criteria, Class<? extends AbstractModel> entityClass) {
        this.criteria = criteria;
        this.entityClass = entityClass;
    }

    public Map<String, Object> getCriteria() {
        return criteria;
    }

    public void setCriteria(Map<String, Object> criteria) {
        this.criteria = criteria;
    }

    public SearchCriteria addCriteria(String field, Object value) {
        if (this.criteria == null) {
            this.criteria = new HashMap<String, Object>();
        }

        this.criteria.put(field, value);
        return this;
    }

    public Class<? extends AbstractModel> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<? extends AbstractModel> entityClass) {
        this.entityClass = entityClass;
    }

    public void setUseAnd(boolean and) {
        this.useAnd = and;
    }

    public boolean isUseAnd() {
        return useAnd;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public boolean contains(String name) {
        return criteria.containsKey(name);
    }

}