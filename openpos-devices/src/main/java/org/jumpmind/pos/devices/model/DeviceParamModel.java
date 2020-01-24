package org.jumpmind.pos.devices.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@Data
@NoArgsConstructor
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

    public DeviceParamModel(String name, String value){
        paramName = name;
        paramValue = value;
    }
}
