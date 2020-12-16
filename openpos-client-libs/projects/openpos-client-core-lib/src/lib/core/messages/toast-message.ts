import { OpenposMessage } from './message';
import { MessageTypes } from './message-types';

export class ToastMessage implements OpenposMessage {
    type = MessageTypes.TOAST;
    willUnblock: boolean;
}
