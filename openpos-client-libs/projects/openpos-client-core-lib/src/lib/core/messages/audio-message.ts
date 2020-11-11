import { OpenposMessage } from './message';
import { AudioOptions } from './audio-options';

export interface AudioMessage extends OpenposMessage, AudioOptions {
    url: string;
}
