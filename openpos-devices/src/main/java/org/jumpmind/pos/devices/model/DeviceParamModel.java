package org.jumpmind.pos.devices.model;

import lombok.Data;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@Data
@TableDef(name="device_param")
public class DeviceParamModel extends AbstractModel {

    @ColumnDef(primaryKey = true)
    private String deviceId;

    @ColumnDef(primaryKey = true)
    private String appId;

    @ColumnDef(primaryKey = true)
    private String paramName;

    @ColumnDef
    private String paramValue;
}
