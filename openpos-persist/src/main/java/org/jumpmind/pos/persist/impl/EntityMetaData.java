package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jumpmind.db.model.Table;

public class EntityMetaData {

    private Table table;
    private List<Field> entityIdFields = new ArrayList<Field>();
    
    public Table getTable() {
        return table;
    }
    public void setTable(Table table) {
        this.table = table;
    }
    public List<Field> getEntityIdFields() {
        return entityIdFields;
    }
    public void setEntityIdFields(List<Field> entityIdFields) {
        this.entityIdFields = entityIdFields;
    }
    
}
