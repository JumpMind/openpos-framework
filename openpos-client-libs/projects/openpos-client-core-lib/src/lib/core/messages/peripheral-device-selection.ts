import { MessageTypes } from './message-types';
import { OpenposMessage } from './message';

export class PeripheralDeviceSelectionMessage implements OpenposMessage {
    type = MessageTypes.PERIPHERAL_DEVICE_SELECTION;
    
    selectedId: string;
    category: PeripheralCategoryDescription;
    available: PeripheralDeviceDescription[];
}

export interface PeripheralDeviceDescription {
    id: string;
    displayName: string;
}

export interface PeripheralCategoryDescription {
    id: string;
    localizationDisplayKey: string;
}
