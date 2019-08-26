import { OpenposMessage } from './message';
import { MessageTypes } from './message-types';

export class ConfigChangedMessage implements OpenposMessage {
    type = MessageTypes.CONFIG_CHANGED;
    constructor( public configType: string) {}
}
