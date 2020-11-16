import { AudioRequest } from './audio-request.interface';

export interface MouseAudioInteraction {
    mouseDown?: AudioRequest;
    mouseUp?: AudioRequest;
}

export interface TouchAudioInteraction {
    touchStart?: AudioRequest;
    touchEnd?: AudioRequest;
}

export interface DialogAudioInteraction {
    opening?: AudioRequest;
    closing?: AudioRequest;
}

export interface AudioInteractionSet {
    enabled: boolean;
    mouse?: MouseAudioInteraction;
    touch?: TouchAudioInteraction;
    dialog?: DialogAudioInteraction;
}