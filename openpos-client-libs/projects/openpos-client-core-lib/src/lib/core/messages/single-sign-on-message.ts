import {IActionItem} from '../actions/action-item.interface';
import {OpenposMessage} from './message';
import {MessageTypes} from './message-types';

export class SingleSignOnMessage implements OpenposMessage {
    type = MessageTypes.SINGLE_SIGN_ON_REQUEST;
    providerName: string;
    responseAction: IActionItem;
}