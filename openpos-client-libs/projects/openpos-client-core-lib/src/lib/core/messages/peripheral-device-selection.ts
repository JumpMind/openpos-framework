import { MessageTypes } from "./message-types";
import { OpenposMessage } from "./message";

export class PeripheralDeviceSelectionMessage implements OpenposMessage {
    type = MessageTypes.PERIPHERAL_DEVICE_SELECTION;

    selectedId: string;
    categoryDisplayName: string;
    available: PeripheralDeviceDescription[];
}

export interface PeripheralDeviceDescription {
    id: string;
    displayName: string;
}
