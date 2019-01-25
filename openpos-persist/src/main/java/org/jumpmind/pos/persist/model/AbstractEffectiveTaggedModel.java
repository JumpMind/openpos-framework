package org.jumpmind.pos.persist.model;

import java.util.Date;

import org.jumpmind.pos.persist.ColumnDef;

abstract public class AbstractEffectiveTaggedModel extends AbstractTaggedModel implements IEffectiveDateModel {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    @ColumnDef(primaryKey=true)
    Date effectiveStartTime;
    
    @ColumnDef 
    Date effectiveEndTime;
    
    @Override
    public void setEffectiveStartTime(Date effectiveStartTime) {
        this.effectiveStartTime = effectiveStartTime;
    }
    
    @Override
    public Date getEffectiveStartTime() {
        return effectiveStartTime;
    }
    
    @Override
    public void setEffectiveEndTime(Date effectiveEndTime) {
        this.effectiveEndTime = effectiveEndTime;
    }
    
    @Override
    public Date getEffectiveEndTime() {
        return effectiveEndTime;
    }
}
