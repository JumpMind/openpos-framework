import {OpenposMessage} from '../../core/messages/message';
import {MessageTypes} from '../../core/messages/message-types';
import {Status} from '../../core/messages/status.enum';

export class StatusMessage implements OpenposMessage {
    type = MessageTypes.STATUS;
    id: string;
    name: string;
    icon: string;
    status: Status;
    message: string;
}