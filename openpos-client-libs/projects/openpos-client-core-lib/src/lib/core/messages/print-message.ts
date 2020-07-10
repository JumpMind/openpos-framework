import {OpenposMessage} from './message';
import {MessageTypes} from './message-types';

export class PrintMessage implements OpenposMessage {
    type = MessageTypes.PRINT;
    html: string;
    printerId: string;
}