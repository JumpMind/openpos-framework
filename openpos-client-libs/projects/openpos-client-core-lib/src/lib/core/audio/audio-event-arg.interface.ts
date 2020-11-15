import { AudioRequest } from './audio.request.interface';

export interface AudioEventArg {
    audio: HTMLAudioElement;
    request: AudioRequest
}