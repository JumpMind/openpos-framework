import {IActionItem} from '../actions/action-item.interface';
import {OpenposMessage} from './message';
import {MessageTypes} from './message-types';

export class LockScreenMessage implements OpenposMessage{
    type = MessageTypes.LOCK_SCREEN;
    imageUrl: string;
    userText: string;
    iconName: string;
    passwordAction: IActionItem;
    errorMessage: string;

}