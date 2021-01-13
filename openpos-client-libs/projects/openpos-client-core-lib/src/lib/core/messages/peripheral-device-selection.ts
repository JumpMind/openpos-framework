import { MessageTypes } from "./message-types";
import { OpenposMessage } from "./message";

export class PeripheralDeviceSelectionMessage implements OpenposMessage {
    type = MessageTypes.PERIPHERAL_DEVICE_SELECTION;

    id: string;
    displayName: string;
    category: string;
}
