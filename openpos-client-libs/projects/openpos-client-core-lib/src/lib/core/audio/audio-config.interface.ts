import { AudioInteractionSet } from './audio-interaction-set.interface';

export interface AudioConfig {
    enabled: boolean;
    volume: number;
    interactions: AudioInteractionSet;
}