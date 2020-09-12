package org.jumpmind.pos.devices.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableDef(name="device_status",
        primaryKey = {"deviceId", "appId"})
public class DeviceStatusModel extends AbstractModel {
    @ColumnDef
    private String deviceId;

    @ColumnDef
    private String appId;

    @ColumnDef
    private String deviceStatus;

}
