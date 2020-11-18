import { AudioRequest } from './audio-request.interface';
import { AudioConfig } from './audio-config.interface';
import { deepAssign } from '../../utilites/deep-assign';

export class AudioUtil {
    static getDefaultRequest(request?: AudioRequest): AudioRequest {
        return {
            url: null,
            sound: null,
            playbackRate: 1,
            startTime: 0,
            endTime: 0,
            loop: false,
            volume: 1,
            autoplay: true,
            delayTime: 0,
            ...request
        };
    }

    static getDefaultConfig(config?: AudioConfig): AudioConfig {
        return {
            enabled: false,
            volume: 1,
            interactions: {
                enabled: true
            },
            ...deepAssign({}, config)
        };
    }

    static getAudioRequestHash(request: AudioRequest): string {
        return `sound:${request.sound}|`
            + `playbackRate:${request.playbackRate}|`
            + `startTime:${request.startTime}|`
            + `endTime:${request.endTime}|`
            + `loop:${request.loop}|`
            + `volume:${request.volume}|`
            + `autoplay:${request.autoplay}|`
            + `delayTime:${request.delayTime}|`
            + `url:${request.url}|`;
    }
    
    static isPlaying(audio: HTMLAudioElement): boolean {
        return audio
            && audio.currentTime > 0
            && !audio.paused
            && !audio.ended
            && audio.readyState > HTMLMediaElement.HAVE_CURRENT_DATA;
    }
}