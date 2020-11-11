import { Injectable, OnDestroy } from '@angular/core';
import { PersonalizationService } from '../personalization/personalization.service';
import { SessionService } from './session.service';
import { merge, Subject } from 'rxjs';
import { MessageTypes } from '../messages/message-types';
import { takeUntil } from 'rxjs/operators';
import { AudioMessage } from '../messages/audio-message';
import { AudioEventArg } from '../messages/audio-event-arg';
import { AudioOptions } from '../messages/audio-options';

@Injectable({
    providedIn: 'root',
})
export class AudioService implements OnDestroy {
    private destroyed$ = new Subject();
    private stop$ = new Subject();

    public beforePlay$ = new Subject<AudioEventArg>();
    public playing$ = new Subject<AudioEventArg>();

    constructor(private sessionService: SessionService, private personalizationService: PersonalizationService) {
    }

    ngOnDestroy() {
        this.destroyed$.next();
    }

    listen(): void {
        console.log(`[AudioService]: Listening for ${MessageTypes.AUDIO} messages...`);

        this.sessionService.getMessages(MessageTypes.AUDIO)
            .pipe(
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(message => this.onReceivedAudioMessage(message));
    }

    stopListening(): void {
        console.log('[AudioService]: Stopped taking requests');
        this.stop$.next();
    }

    play(url: string, options: AudioOptions = {}): HTMLAudioElement {
        const audio = this.createAudio(url, options);

        this.beforePlay$.next({audio, options});
        options = this.getOptionsWithDefaults(options);

        if (options.autoplay === true) {
            if (options.delayTime) {
                window.setTimeout(() => audio.play(), options.delayTime * 1000);
            } else {
                audio.play();
            }

            console.log('[AudioService]: Playing', audio, options);
            this.playing$.next({audio, options});
        }

        return audio;
    }

    createAudio(url: string, options: AudioOptions = {}): HTMLAudioElement {
        options = this.getOptionsWithDefaults(options);

        const actualUrl = this.personalizationService.replaceTokens(url);
        const audio = new Audio(actualUrl);

        audio.volume = options.volume;
        audio.autoplay = options.autoplay && !options.delayTime;
        audio.currentTime = options.startTime;
        audio.playbackRate = options.playbackRate;
        audio.loop = options.loop;

        if (options.endTime) {
            audio.addEventListener('timeupdate', () => {
                if (audio.currentTime >= options.endTime) {
                    audio.pause();
                }
            });
        }

        return audio;
    }

    getOptionsWithDefaults(options: AudioOptions = {}): AudioOptions {
        return {
            playbackRate: 1,
            startTime: 0,
            endTime: 0,
            loop: false,
            volume: 1,
            autoplay: true,
            delayTime: 0,
            ...options
        };
    }

    onReceivedAudioMessage(message: AudioMessage): void {
        console.log('[AudioService]: Received request', message);

        const url = message.url;
        const options = {url, ...message};

        this.play(url, options);
    }
}