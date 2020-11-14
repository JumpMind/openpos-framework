import { Injectable, OnDestroy } from '@angular/core';
import { merge, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AudioEventArg } from './audio-event-arg';
import { SessionService } from '../core/services/session.service';
import { PersonalizationService } from '../core/personalization/personalization.service';
import { MessageTypes } from '../core/messages/message-types';
import { AudioRequest } from './audio.request.interface';
import { AudioMessage } from './audio-message.interface';
import { AudioConfigMessage } from './audio-config-message.interface';

@Injectable({
    providedIn: 'root',
})
export class AudioService implements OnDestroy {
    private destroyed$ = new Subject();
    private stop$ = new Subject();
    private enabled = true;

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
            ).subscribe(message => this.onAudioMessage(message));

        this.sessionService.getMessages(MessageTypes.AUDIO_CONFIG)
            .pipe(
                takeUntil(merge(this.stop$, this.destroyed$))
            ).subscribe(message => this.onAudioConfigMessage(message));
    }

    stopListening(): void {
        console.log('[AudioService]: Stopped taking requests');
        this.stop$.next();
    }

    play(request: AudioRequest | string): HTMLAudioElement {
        if (!this.enabled) {
            console.warn('[AudioService]: Ignoring audio request because the service is disabled', request);
        }

        request = typeof request === 'string' ? {sound: request} : request;
        request = {...AudioService.getRequestDefaults(), ...request};
        const audio = this.createAudio(request);

        this.beforePlay$.next({audio, request});

        if (request.autoplay === true) {
            if (request.delayTime) {
                window.setTimeout(() => audio.play(), request.delayTime * 1000);
            } else {
                audio.play();
            }

            console.log('[AudioService]: Playing', audio, request);
            this.playing$.next({audio, request});
        }

        return audio;
    }

    createAudio(request: AudioRequest): HTMLAudioElement {
        request = {...AudioService.getRequestDefaults(), ...request};

        const url = this.personalizationService.replaceTokens(request.sound);
        const audio = new Audio(url);

        audio.volume = request.volume;
        audio.autoplay = request.autoplay && !request.delayTime;
        audio.currentTime = request.startTime;
        audio.playbackRate = request.playbackRate;
        audio.loop = request.loop;

        if (request.endTime) {
            audio.addEventListener('timeupdate', () => {
                if (audio.currentTime >= request.endTime) {
                    audio.pause();
                }
            });
        }

        return audio;
    }

    onAudioMessage(message: AudioMessage): void {
        console.log('[AudioService]: Received request', message);
        this.play(message);
    }

    onAudioConfigMessage(message: AudioConfigMessage): void {
        console.log('[AudioService]: Received config message', message);

        if (this.enabled && !message.enabled) {
            this.stopListening();
        }

        if (!this.enabled && message.enabled) {
            this.listen();
        }

        this.enabled = message.enabled;
    }

    static getRequestDefaults(): AudioRequest {
        return {
            sound: '',
            playbackRate: 1,
            startTime: 0,
            endTime: 0,
            loop: false,
            volume: 1,
            autoplay: true,
            delayTime: 0,
            reverse: false
        };
    }
}