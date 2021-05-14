package org.jumpmind.pos.util.peripheral;

import lombok.*;
import org.jumpmind.pos.util.model.Message;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class PeripheralDeviceSelectionMessage extends Message {
    CategoryDescriptor category;
    List<PeripheralDeviceDescription> available;
    PeripheralDeviceDescription selectedDevice;
    boolean enabled;

    @Override
    public String getType() {
        return "PeripheralDeviceSelection";
    }
}
