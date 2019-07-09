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
import org.springframework.util.Assert;

public class Query<T> {

    private String name;
    private Class<? extends T> resultClass;
    private boolean useAnd = true;

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
    
    
    public Query<T> named(String name) {
        this.name = name;
        return this;
    }

    public Query<T> useAnd(boolean useAnd) {
        this.useAnd = useAnd;
        return this;
    }
    
    public boolean isUseAnd() {
        return useAnd;
    }

    public String getName() {
        return name;
    }


}
