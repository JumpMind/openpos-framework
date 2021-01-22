package org.jumpmind.pos.util.peripheral;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeripheralDeviceDescription {
    String id;
    String displayName;
}
