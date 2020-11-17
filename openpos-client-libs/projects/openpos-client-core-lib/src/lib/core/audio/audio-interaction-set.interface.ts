import { AudioRequest } from './audio-request.interface';

export interface MouseAudioInteraction {
    enabled?: boolean;
    mouseDown?: AudioRequest;
    mouseUp?: AudioRequest;
}

export interface TouchAudioInteraction {
    enabled?: boolean;
    touchStart?: AudioRequest;
    touchEnd?: AudioRequest;
}

export interface DialogAudioInteraction {
    enabled?: boolean;
    opening?: AudioRequest;
    closing?: AudioRequest;
}

export interface AudioInteractionSet {
    enabled: boolean;
    mouse?: MouseAudioInteraction;
    touch?: TouchAudioInteraction;
    dialog?: DialogAudioInteraction;
}