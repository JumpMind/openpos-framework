import { OpenposMessage } from '../messages/message';
import { AudioRequest } from './audio-request.interface';

export interface AudioMessage extends OpenposMessage {
    request: AudioRequest;
}