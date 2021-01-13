package org.jumpmind.pos.util.peripheral;

import lombok.*;
import org.jumpmind.pos.util.model.Message;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class PeripheralDeviceSelectionMessage extends Message {
    String category;
    String id;
    String displayName;

    @Override
    public String getType() {
        return "PeripheralDeviceSelection";
    }
}
