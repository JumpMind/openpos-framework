export interface AudioGroup {
    [key: string]: HTMLAudioElement;
}

export interface AudioRequest {
    sound: string;
    playbackRate?: number;
    startTime?: number;
    endTime?: number;
    loop?: boolean;
    volume?: number;
    autoplay?: boolean;
    delayTime?: number;
    group?: string;
    waitForScreen?: boolean;
    waitForDialog?: boolean;
}
