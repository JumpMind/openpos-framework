export interface AudioRequest {
    sound?: string;
    playbackRate?: number;
    startTime?: number;
    endTime?: number;
    loop?: boolean;
    volume?: number;
    autoplay?: boolean;
    delayTime?: number;
    reverse?: boolean;
}
