import { MessageTypes } from './message-types';
import { OpenposMessage } from './message';

export class ToastMessage implements OpenposMessage {
    type = MessageTypes.TOAST;
}
