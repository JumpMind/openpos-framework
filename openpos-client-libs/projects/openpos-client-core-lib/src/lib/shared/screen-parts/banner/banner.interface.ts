import { IActionItem } from '../../../core/interfaces/action-item.interface';

export interface BannerInterface {
    iconName: string;
    messageType: MessageType;
    messages: string[];
    buttons: IActionItem[];
}

export enum MessageType {
    INFO = 'INFO',
    WARNING = 'WARNING', 
    ERROR = 'ERROR'
}
