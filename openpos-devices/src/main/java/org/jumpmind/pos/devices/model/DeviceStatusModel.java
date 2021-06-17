package org.jumpmind.pos.devices.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableDef(name="device_status",
        primaryKey = {"deviceId"})
public class DeviceStatusModel extends AbstractModel {
    @ColumnDef
    private String deviceId;

    @ColumnDef
    private String deviceStatus;

}
