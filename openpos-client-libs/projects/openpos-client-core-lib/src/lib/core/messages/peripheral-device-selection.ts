import { MessageTypes } from "./message-types";
import { OpenposMessage } from "./message";

export class PeripheralDeviceSelectionMessage implements OpenposMessage {
    type = MessageTypes.PERIPHERAL_DEVICE_SELECTION;

    selectedId: string;
    categoryDisplayName: string;
    available: PeripheralDeviceDescription[];
}

export class PeripheralChangeSelectionRequestMessage implements OpenposMessage {
    type = MessageTypes.PERIPHERAL_CHANGE_SELECTION_REQUEST;

    category: string;
    id: string;
}

export interface PeripheralDeviceDescription {
    id: string;
    displayName: string;
}
