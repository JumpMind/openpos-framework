package org.jumpmind.pos.devices.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableDef(name = "device_personalization", primaryKey = {"deviceName"})
public class DevicePersonalizationModel extends AbstractModel {
    @ColumnDef
    private String deviceName;
    @ColumnDef
    private String serverAddress;
    @ColumnDef
    private String serverPort;
    @ColumnDef
    private String deviceId;
    @ColumnDef
    private String appId;

    private List<DeviceParamModel> deviceParamModels;
}
