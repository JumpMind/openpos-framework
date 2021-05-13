package org.jumpmind.pos.devices.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableDef(name="device_param",
        primaryKey = {"deviceId", "paramName"})
public class DeviceParamModel extends AbstractModel {

    @ColumnDef
    private String deviceId;

    @ColumnDef
    private String appId;

    @ColumnDef
    private String paramName;

    @ColumnDef
    private String paramValue;

    public DeviceParamModel(String name, String value){
        paramName = name;
        paramValue = value;
    }
}
