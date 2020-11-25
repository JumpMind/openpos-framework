import { AudioRequest } from './audio-request.interface';

export interface AudioPlayRequest {
    audio: HTMLAudioElement;
    request: AudioRequest
}