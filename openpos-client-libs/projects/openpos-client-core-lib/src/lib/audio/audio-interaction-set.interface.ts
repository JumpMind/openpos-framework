import { AudioRequest } from './audio.request.interface';

export interface MouseAudioInteraction {
    clickIn: AudioRequest;
    clickOut: AudioRequest;
}

export interface DialogAudioInteraction {
    opening: AudioRequest;
    closing: AudioRequest;
}

export interface AudioInteractionSet {
    enabled: boolean;
    mouse: MouseAudioInteraction;
    dialog: DialogAudioInteraction;
}