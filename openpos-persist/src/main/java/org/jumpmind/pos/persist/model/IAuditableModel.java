package org.jumpmind.pos.persist.model;

import java.util.Date;

public interface IAuditableModel {
    public Date getCreateTime();
    public void setCreateTime(Date createTime);

    public String getCreateBy();
    public void setCreateBy(String createBy);

    public Date getLastUpdateTime();
    public void setLastUpdateTime(Date lastUpdateTime);

    public String getLastUpdateBy();
    public void setLastUpdateBy(String lastUpdateBy);
}
