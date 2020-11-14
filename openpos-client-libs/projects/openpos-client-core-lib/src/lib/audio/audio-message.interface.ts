import { AudioRequest } from './audio.request.interface';
import { OpenposMessage } from '../core/messages/message';

export interface AudioMessage extends OpenposMessage, AudioRequest {

}
