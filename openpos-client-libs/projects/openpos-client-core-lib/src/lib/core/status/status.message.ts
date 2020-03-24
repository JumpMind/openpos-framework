import {OpenposMessage} from '../messages/message';
import {MessageTypes} from '../messages/message-types';
import {Status} from '../messages/status.enum';

export class StatusMessage implements OpenposMessage {
    type = MessageTypes.STATUS;
    id: string;
    name: string;
    icon: string;
    status: Status;
    message: string;
}