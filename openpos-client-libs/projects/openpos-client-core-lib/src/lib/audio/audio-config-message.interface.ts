import { OpenposMessage } from '../core/messages/message';
import { AudioInteractionSet } from './audio-interaction-set.interface';

export interface AudioConfigMessage extends OpenposMessage {
    enabled: boolean;
    interactions: AudioInteractionSet;
}
