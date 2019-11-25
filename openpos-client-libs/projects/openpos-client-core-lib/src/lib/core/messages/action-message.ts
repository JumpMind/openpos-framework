import { OpenposMessage } from './message';
import { MessageTypes } from './message-types';

export class ActionMessage implements OpenposMessage {
    type = MessageTypes.ACTION;
    constructor( public actionName: string, doNotBlockForResponse: boolean, public payload?: any ) {}
}
