import { OpenposMessage } from '../messages/message';
import { AudioConfig } from './audio-config.interface';

export interface AudioConfigMessage extends OpenposMessage {
    config: AudioConfig;
}
