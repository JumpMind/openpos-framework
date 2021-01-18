package org.jumpmind.pos.util.peripheral;

import lombok.*;
import org.jumpmind.pos.util.model.Message;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class PeripheralDeviceSelectionMessage extends Message {
    String categoryDisplayName;
    List<PeripheralDeviceDescription> available;
    String selectedId;

    @Override
    public String getType() {
        return "PeripheralDeviceSelection";
    }
}
