package org.jumpmind.pos.persist.model;

import java.util.Date;

public interface IEffectiveDateModel {
    
    public void setEffectiveStartTime(Date effectiveStartTime);
    public Date getEffectiveStartTime();
    
    public void setEffectiveEndTime(Date effectiveEndTime);
    public Date getEffectiveEndTime();

}
