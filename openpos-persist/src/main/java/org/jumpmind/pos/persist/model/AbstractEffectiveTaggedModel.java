package org.jumpmind.pos.persist.model;

import java.util.Date;

import org.jumpmind.pos.persist.ColumnDef;

abstract public class AbstractEffectiveTaggedModel extends AbstractTaggedModel {

    private static final long serialVersionUID = 1L;

    @ColumnDef(primaryKey=true)
    Date effectiveStartTime;
    
    @ColumnDef 
    Date effectiveEndTime;
    
    public void setEffectiveStartTime(Date effectiveStartTime) {
        this.effectiveStartTime = effectiveStartTime;
    }
    
    public Date getEffectiveStartTime() {
        return effectiveStartTime;
    }
    
    public void setEffectiveEndTime(Date effectiveEndTime) {
        this.effectiveEndTime = effectiveEndTime;
    }
    
    public Date getEffectiveEndTime() {
        return effectiveEndTime;
    }
}
