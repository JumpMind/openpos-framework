package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.UIMessage;

@Data
public class SimulatedPeripheralUIMessage extends UIMessage {
    private String receiptPlaceholder;

    public SimulatedPeripheralUIMessage() {
        this.setId("simulatedPeripheral");
        this.setScreenType(UIMessageType.SIMULATED_PERIPHERAL);
    }
}
